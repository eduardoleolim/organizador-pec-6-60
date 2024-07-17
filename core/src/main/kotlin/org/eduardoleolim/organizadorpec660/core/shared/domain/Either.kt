package org.eduardoleolim.organizadorpec660.core.shared.domain

sealed class Either<out L, out R> {
    abstract fun <C> fold(ifLeft: (L) -> C, ifRight: (R) -> C): C
    abstract suspend fun <C> foldAsync(ifLeft: suspend (L) -> C, ifRight: suspend (R) -> C): C
}

data class Right<out R>(val value: R) : Either<Nothing, R>() {
    override fun <C> fold(ifLeft: (Nothing) -> C, ifRight: (R) -> C): C = ifRight(value)
    override suspend fun <C> foldAsync(ifLeft: suspend (Nothing) -> C, ifRight: suspend (R) -> C): C = ifRight(value)
}

data class Left<out L>(val value: L) : Either<L, Nothing>() {
    override fun <C> fold(ifLeft: (L) -> C, ifRight: (Nothing) -> C): C = ifLeft(value)
    override suspend fun <C> foldAsync(ifLeft: suspend (L) -> C, ifRight: suspend (Nothing) -> C): C = ifLeft(value)
}

inline fun <L, R> Either<L, R>.onRight(action: (R) -> Unit): Either<L, R> {
    if (this is Right<R>) {
        action(this.value)
    }
    return this
}

inline fun <L, R> Either<L, R>.onLeft(action: (L) -> Unit): Either<L, R> {
    if (this is Left<L>) {
        action(this.value)
    }
    return this
}
