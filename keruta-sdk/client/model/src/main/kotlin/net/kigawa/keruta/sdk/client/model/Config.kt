package net.kigawa.keruta.sdk.client.model

import kotlin.time.Duration

interface Config {
    val apiToken: ApiToken
    val timeout: Duration
}
