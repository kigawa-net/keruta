package net.kigawa.kodel.api.err

/**
 * 結果を表すシールドインターフェース。
 * 成功（Ok）またはエラー（Err）を表す。
 *
 * @param T 成功時の値の型
 * @param E エラーの型
 */
sealed interface Res<out T, out E: Throwable> {
    /**
     * 成功を表すクラス。
     *
     * @param value 成功時の値
     */
    class Ok<out T, out E: Throwable>(val value: T): Res<T, E> {
        fun <F: Throwable> convertType(): Ok<T, F> = Ok(value)
        fun <F> mapValue(block: (T) -> F): Ok<F, E> = Ok(block(value))
    }

    /**
     * エラーを表すクラス。
     *
     * @param err エラー
     */
    class Err<out T, out E: Throwable>(val err: E): Res<T, E> {
        fun <U> convertType(): Err<U, E> = Err(err)
        fun <U, F: Throwable> mapErr(block: (E) -> F): Err<U, F> = Err(block(err))
    }
}
