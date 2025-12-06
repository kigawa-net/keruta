package net.kigawa.keruta.sdk.client.model.request

import net.kigawa.keruta.sdk.client.model.err.KerutaErr
import java.util.UUID
import kotlin.time.Duration

class ReceiveTimeoutErr(val id: UUID, val timeout: Duration): KerutaErr() {
}
