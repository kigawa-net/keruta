package net.kigawa.keruta.sdk.client.model.auth

import net.kigawa.keruta.sdk.client.model.err.KerutaErr
import net.kigawa.keruta.sdk.client.model.request.RequestErr

class AuthErr(val error: RequestErr): KerutaErr() {
}
