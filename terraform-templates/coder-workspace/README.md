# Coder Workspace Persistent Storage Template

このTerraformテンプレートは、CoderワークスペースでKubernetes上に永続化ストレージを作成するためのものです。

## 機能

- ユーザー固有の永続ボリューム要求（PVC）の作成
- ReadWriteMany アクセスモードでの共有ストレージ
- `/home/coder/shared` へのマウント
- Coder UI上でのストレージ情報の表示

## 使用方法

### 必要な環境

- Coder テンプレート内でのTerraform実行環境
- Kubernetes クラスターへのアクセス権限
- ReadWriteMany をサポートするストレージクラス

### パラメータ

| パラメータ | 説明 | デフォルト値 |
|-----------|------|-------------|
| `storage_class_name` | 使用するストレージクラス名 | `standard` |

### テンプレートでの使用例

```hcl
# Coderワークスペーステンプレート内で使用
module "persistent_storage" {
  source = "./terraform-templates/coder-workspace"
  
  storage_class_name = "nfs-client" # または利用可能なストレージクラス
}

# Pod定義内でPVCをマウント
resource "kubernetes_pod" "workspace" {
  # ... その他の設定 ...
  
  spec {
    container {
      name  = "coder"
      image = "codercom/code-server:latest"
      
      volume_mount {
        name       = "shared-storage"
        mount_path = "/home/coder/shared"
      }
    }
    
    volume {
      name = "shared-storage"
      persistent_volume_claim {
        claim_name = module.persistent_storage.pvc_name
      }
    }
  }
}
```

## ストレージクラスの要件

このテンプレートは `ReadWriteMany` アクセスモードを必要とします。以下のストレージクラスが推奨されます：

- NFS (Network File System)
- CephFS
- GlusterFS
- Azure Files
- AWS EFS (via CSI driver)

## セキュリティ考慮事項

- PVCは各ユーザーごとに作成されますが、名前空間内では共有される可能性があります
- 適切なRBACポリシーを設定して、他のユーザーのPVCへのアクセスを制限してください
- 機密データについては、暗号化されたストレージクラスの使用を検討してください

## トラブルシューティング

### PVCが Pending 状態で止まる場合

1. ストレージクラスが存在することを確認
2. ReadWriteMany をサポートしているか確認
3. 十分なストレージ容量があるか確認

### マウントエラーが発生する場合

1. Kubernetes NodeでのPVマウント権限を確認
2. ストレージクラスのプロビジョナーが正常に動作しているか確認
3. Pod SecurityContextの設定を確認

## 関連ファイル

- `main.tf`: メインのTerraform設定
- このREADME.md: ドキュメント