# kdse ドキュメントサーバー 実装計画

## 実装方針

構造化フィールドを持つドキュメントの CRUD・バージョン管理・検索を提供する Ktor サービス。
フィールド値は `JsonElement` として扱い、`FieldDefinition` による型検証を行う。
バージョン管理は全履歴を `document_versions` テーブルに JSON スナップショットとして保持する。

## ファイル・クラス構成

```
kdse/
├── build.gradle.kts
├── src/main/kotlin/net/kigawa/keruta/kdse/
│   ├── Main.kt
│   ├── KerutaDocumentServer.kt
│   ├── config/
│   │   ├── KdseConfig.kt
│   │   └── KdseConfigLoader.kt
│   ├── api/
│   │   ├── HealthRoutes.kt
│   │   ├── ErrorResponse.kt
│   │   └── document/
│   │       ├── DocumentDtos.kt
│   │       ├── DocumentRoutes.kt
│   │       └── DocumentMappers.kt
│   ├── domain/
│   │   ├── err/
│   │   │   └── KdseErr.kt
│   │   ├── document/
│   │   │   ├── Document.kt
│   │   │   ├── DocumentFieldValue.kt
│   │   │   ├── DocumentVersion.kt
│   │   │   └── DocumentRepository.kt
│   │   └── field/
│   │       ├── FieldDefinition.kt
│   │       ├── FieldType.kt
│   │       └── FieldDefinitionRepository.kt
│   ├── usecase/
│   │   ├── document/
│   │   │   ├── CreateDocumentUseCase.kt
│   │   │   ├── GetDocumentUseCase.kt
│   │   │   ├── UpdateDocumentUseCase.kt
│   │   │   ├── DeleteDocumentUseCase.kt
│   │   │   ├── ListDocumentVersionsUseCase.kt
│   │   │   └── RestoreDocumentVersionUseCase.kt
│   │   ├── field/
│   │   │   ├── CreateFieldDefinitionUseCase.kt
│   │   │   ├── GetFieldDefinitionUseCase.kt
│   │   │   └── ListFieldDefinitionsUseCase.kt
│   │   └── search/
│   │       └── SearchDocumentsUseCase.kt
│   ├── persist/
│   │   ├── db/
│   │   │   ├── KdseDbPersister.kt
│   │   │   └── KdseFlywayMigrator.kt
│   │   ├── table/
│   │   │   ├── DocumentsTable.kt
│   │   │   ├── DocumentVersionsTable.kt
│   │   │   └── FieldDefinitionsTable.kt
│   │   ├── ExposedDocumentRepository.kt
│   │   └── ExposedFieldDefinitionRepository.kt
│   └── search/
│       ├── DocumentSearchQuery.kt
│       ├── DocumentSearchRepository.kt
│       └── ExposedDocumentSearchRepository.kt
└── src/main/resources/
    ├── application.yaml
    └── db/migration/
        └── V1__create_kdse_tables.sql
```

## レイヤー依存

```
api → usecase → domain ← persist
                        ← search
```

禁止: `domain → api`, `domain → persist`, `usecase → ktor`, `usecase → exposed`, `api → exposed`

## domain 層

### Document

```kotlin
@Serializable
data class Document(
    val id: String,
    val version: Long,
    val fields: List<DocumentFieldValue>,
)
```

### DocumentFieldValue

```kotlin
@Serializable
data class DocumentFieldValue(
    val fieldId: String,
    val value: JsonElement,
)
```

### DocumentVersion

```kotlin
@Serializable
data class DocumentVersion(
    val documentId: String,
    val version: Long,
    val fields: List<DocumentFieldValue>,
)
```

### FieldDefinition / FieldType

```kotlin
@Serializable
data class FieldDefinition(
    val id: String,
    val name: String,
    val type: FieldType,
    val required: Boolean = false,
)

@Serializable
enum class FieldType { STRING, NUMBER, BOOLEAN, LIST, OBJECT, JSON }
```

### KdseErr

```kotlin
sealed class KdseErr : Throwable() {
    data class DocumentNotFound(val id: String) : KdseErr()
    data class FieldDefinitionNotFound(val fieldId: String) : KdseErr()
    data class FieldDuplicated(val fieldId: String) : KdseErr()
    data class FieldTypeMismatch(val fieldId: String) : KdseErr()
    data class InvalidSearchOperator(val operator: String) : KdseErr()
    data class Internal(val message: String) : KdseErr()
}
```

## persist 層（DBスキーマ）

```sql
CREATE TABLE documents (
    id VARCHAR(64) PRIMARY KEY,
    current_version BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE document_versions (
    document_id VARCHAR(64) NOT NULL,
    version     BIGINT NOT NULL,
    document_json JSON NOT NULL,
    PRIMARY KEY (document_id, version),
    CONSTRAINT fk_document_versions_document
        FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);

CREATE TABLE field_definitions (
    id         VARCHAR(128) PRIMARY KEY,
    field_json JSON NOT NULL
);
```

## usecase 層の主要ロジック

### CreateDocumentUseCase

1. UUID で ID 採番
2. version = 1
3. fieldId 重複チェック
4. FieldDefinition に基づく型検証
5. `documents` に保存
6. `document_versions` に v1 スナップショット保存

### UpdateDocumentUseCase（patch semantics）

1. 既存ドキュメント取得
2. 指定フィールドのみ上書きマージ
3. version + 1
4. 型検証
5. `documents` 更新
6. `document_versions` にスナップショット追加

### RestoreDocumentVersionUseCase

1. 指定 version 取得
2. 現在 version + 1 を新バージョンとして作成
3. 指定 version の fields を最新版として保存

### SearchDocumentsUseCase

MVP 対応オペレーター: `EQ`, `CONTAINS`, `EXISTS`

## api 層

### エンドポイント

```
POST   /api/documents
GET    /api/documents/{id}
PATCH  /api/documents/{id}
DELETE /api/documents/{id}
GET    /api/documents/{id}/versions
GET    /api/documents/{id}/versions/{version}
POST   /api/documents/{id}/versions/{version}/restore
POST   /api/documents/search
POST   /api/fields
GET    /api/fields
GET    /api/fields/{id}
PUT    /api/fields/{id}
DELETE /api/fields/{id}
```

## 実装順序

1. `kdse/build.gradle.kts` + `settings.gradle.kts` 追加
2. domain 層（モデル・リポジトリインターフェース・エラー型）
3. DB マイグレーション SQL
4. persist 層（Exposed テーブル・リポジトリ実装）
5. search 層（DocumentSearchQuery・ExposedDocumentSearchRepository）
6. usecase 層（create → get → update → delete → versions → restore → search → field CRUD）
7. api 層（DTO・マッパー・ルート）
8. config / KerutaDocumentServer 組み立て・Main.kt
9. テスト（usecase / repository / routes）

## テスト方針

- usecase テスト: MockK でリポジトリをモック
- repository テスト: testcontainers または compose.test.yml の MySQL
- routes テスト: Ktor testApplication

## 関連

- Issue: #435
