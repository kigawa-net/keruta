package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedArg
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedMsg

class SendQueueListedArg(
    override val msg: ClientQueueListedMsg,
): ClientQueueListedArg
