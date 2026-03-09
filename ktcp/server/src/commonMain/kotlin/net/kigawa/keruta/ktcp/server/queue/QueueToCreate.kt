package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateMsg

class QueueToCreate(
    val input: ServerQueueCreateMsg,
) {
    val setting: String by input::setting
    val name: String by input::name
}
