package net.kigawa.kodel.api.routing

abstract class RouteGroup<R> {
    var routes: List<R> = emptyList()
        private set
    var groups: List<RouteGroup<R>> = emptyList()
        private set

    fun <T: Routes<R, *>> subRoutes(initRoutes: () -> T): T = initRoutes()
        .also { routes += it.ownRoute }

    fun <T: RouteGroup<R>> group(initGroup: () -> T): T = initGroup()
        .also { groups += it }

    fun <T: R> route(initRoute: () -> T): T = initRoute()
        .also { routes += it }
}
