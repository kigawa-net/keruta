package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.entrypoint.ApiEntrypoints
import net.kigawa.keruta.sdk.client.model.utl.entrypoint.NormalEntrypointGroupInfo

interface CreateEntrypointAction {
    val apiEndpoints: ApiEntrypoints
    fun createRoute(): NormalEntrypointGroupInfo<ApiPacketReceiveAction, Any> {
        return apiEndpoints.info
    }
}
