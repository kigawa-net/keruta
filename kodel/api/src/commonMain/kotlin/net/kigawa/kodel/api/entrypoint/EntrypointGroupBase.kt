package net.kigawa.kodel.api.entrypoint

/**
 * エントリーポイントグループのベースクラス。
 * サブエントリーポイントを管理する。
 *
 * @param I 入力の型
 * @param O 出力の型
 */
abstract class EntrypointGroupBase<I, O, C>: Entrypoint<I, O, C> {
    private var subEntrypoints = mutableListOf<Entrypoint<I, O, C>>()

    /** サブエントリーポイントのリスト */
    val entrypoints: List<Entrypoint<I, O, C>>
        get() = subEntrypoints

    /**
     * エントリーポイントを追加する。
     *
     * @param J 追加するエントリーポイントの入力型
     * @param P 追加するエントリーポイントの出力型
     * @param T エントリーポイントの型
     * @param endpoint 追加するエントリーポイント
     * @param translator トランスレーター関数
     * @return 追加されたエントリーポイント
     */
    fun <J, P, T: Entrypoint<J, P, C>> add(
        endpoint: T,
        translator: ((J?) -> P?).(I) -> O?,
    ): T {
        return endpoint.also { subEntrypoints += TranslateEntrypoint(endpoint, translator) }
    }

    override fun access(
        input: I, ctx: C,
    ): O? {
        entrypoints.forEach { entrypoint ->
            entrypoint.access(input, ctx)?.let { return it }
        }
        return onSubEntrypointNotFound(input)
    }

    abstract fun onSubEntrypointNotFound(input: I): O?
}
