package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.event.ApiEvent

interface ApiReceiveEventAction {
    fun receive(event: ApiEvent) {}
}
