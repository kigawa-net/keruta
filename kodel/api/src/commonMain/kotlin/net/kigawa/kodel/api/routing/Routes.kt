package net.kigawa.kodel.api.routing

abstract class Routes<R, C>: RouteGroup<C>() {
    abstract val ownRoute: R
}
