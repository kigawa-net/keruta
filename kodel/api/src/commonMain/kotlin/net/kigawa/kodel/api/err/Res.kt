package net.kigawa.kodel.api.err

/**
 * 結果を表すシールドインターフェース。
 * 成功（Ok）またはエラー（Err）を表す。
 *
 * @param T 成功時の値の型
 * @param E エラーの型
 */
@Suppress("unused")
sealed interface Res<out T, out E: Throwable> {

    /**
     * 成功を表すクラス。
     *
     * @param value 成功時の値
     */
    class Ok<out T, out E: Throwable>(val value: T): Res<T, E> {
        fun <F: Throwable> convert(): Ok<T, F> = Ok(value)
        fun <F> mapValue(block: (T) -> F): Ok<F, E> = Ok(block(value))
    }

    /**
     * エラーを表すクラス。
     *
     * @param err エラー
     */
    class Err<out T, out E: Throwable>(val err: E): Res<T, E> {
        fun <U> convert(): Err<U, E> = Err(err)
        fun <U, F: Throwable> mapErr(block: (E) -> F): Err<U, F> = Err(block(err))
    }
}

@Suppress("unused")
fun <T, E: Throwable> Res<T, E>.convertUnitOk(): Res<Unit, E> = when (val res = this) {
    is Res.Err<T, E> -> res.convert()
    is Res.Ok<T, E> -> Res.Ok(Unit)
}

inline fun <T, E: Throwable, U> Res<T, E>.convertOk(block: (T) -> U): Res<U, E> = when (val res = this) {
    is Res.Err<T, E> -> Res.Err(res.err)
    is Res.Ok<T, E> -> Res.Ok(block(res.value))
}

inline fun <T, E: Throwable, F: Throwable> Res<T, E>.convertErr(block: (E) -> F): Res<T, F> = when (val res = this) {
    is Res.Err<T, E> -> Res.Err(block(res.err))
    is Res.Ok<T, E> -> Res.Ok(res.value)
}

fun <T, E: Throwable> Res<Res<T, E>, E>.flat(): Res<T, E> = when (val res = this) {
    is Res.Err<Res<T, E>, E> -> res.convert()
    is Res.Ok<Res<T, E>, E> -> res.value
}

fun <T, E: Throwable> Res<Res<T, E>?, E>.flatNullable(): Res<T, E>? = when (val res = this) {
    is Res.Err<Res<T, E>?, E> -> res.convert()
    is Res.Ok<Res<T, E>?, E> -> res.value
}

@Suppress("unused")
inline fun <T, E: Throwable, U> Res<T, E>.flatConvertOk(block: (T) -> Res<U, E>): Res<U, E> = convertOk(block)
    .flat()

@Suppress("unused")
inline fun <T, E: Throwable, U> Res<T, E>.flatNullableOk(block: (T) -> Res<U, E>?): Res<U, E>? =
    convertOk(block).flatNullable()

@Suppress("unused")
inline fun <reified T, reified E: Throwable, R> Res<T, E>.whenOkErr(onOk: (T) -> R, onErr: (E) -> R): R = when (this) {
    is Res.Ok<T, E> -> onOk(value)
    is Res.Err<T, E> -> onErr(err)
}

inline fun <reified T, reified E: Throwable, R> Res<T, E>.whenErrOk(onErr: (E) -> R, onOk: (T) -> R): R = when (this) {
    is Res.Ok<T, E> -> onOk(value)
    is Res.Err<T, E> -> onErr(err)
}
inline fun <reified T, reified E: Throwable> Res<T, E>.unwrap(onErr: (E) -> T): T = when (this) {
    is Res.Ok<T, E> -> value
    is Res.Err<T, E> -> onErr(err)
}

fun <T, U, E: Throwable> Res<T, E>.with(res: Res<U, E>): Res<Pair<T, U>, E> {
    val a = when (val r = this) {
        is Res.Err -> return r.convert()
        is Res.Ok -> r.value
    }
    return res.convertOk {
        a to it
    }
}

fun <T, E: Throwable> T.ok(): Res.Ok<T, E> = Res.Ok(this)
fun <T, E: Throwable> E.err(): Res.Err<T, E> = Res.Err(this)
