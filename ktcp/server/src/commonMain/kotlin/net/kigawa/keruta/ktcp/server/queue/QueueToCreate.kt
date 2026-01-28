package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg

class QueueToCreate(
    val input: ServerQueueCreateMsg,
) {
    val providerId: Long by input::providerId
    val name: String by input::name
}
