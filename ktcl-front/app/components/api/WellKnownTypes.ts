export type KtclPropertyJson =
    | { type: 'StringPropertyJson'; nullable: boolean }
    | { type: 'NumberPropertyJson' }
    | { type: 'ObjectPropertyJson'; fields: ObjectPropertyJsonField[] }
    | { type: 'ArrayPropertyJson'; value: KtclPropertyJson }

export interface ObjectPropertyJsonField {
    fieldId: string
    fieldName: string
    value: KtclPropertyJson
}

export interface ObjectPropertyJson {
    fields: ObjectPropertyJsonField[]
}

export interface KerutaWellKnownJson {
    version: string
    issuer: string
    login: string
    queueProperties: ObjectPropertyJson
}