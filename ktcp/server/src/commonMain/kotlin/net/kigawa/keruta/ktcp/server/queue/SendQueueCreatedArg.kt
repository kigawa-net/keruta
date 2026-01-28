package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedArg
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedMsg

class SendQueueCreatedArg(
    override val msg: ClientQueueCreatedMsg,
): ClientQueueCreatedArg
