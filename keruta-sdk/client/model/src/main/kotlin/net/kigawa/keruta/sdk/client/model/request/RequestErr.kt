package net.kigawa.keruta.sdk.client.model.request

import net.kigawa.keruta.sdk.client.model.err.KerutaErr

class RequestErr(val request: ApiRequest, val error: ReceiveTimeoutErr): KerutaErr() {
}
