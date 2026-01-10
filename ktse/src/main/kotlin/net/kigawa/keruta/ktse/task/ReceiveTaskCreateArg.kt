package net.kigawa.keruta.ktse.task

import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateMsg

class ReceiveTaskCreateArg(
    override val taskCreateMsg: ServerTaskCreateMsg,
): ServerTaskCreateArg
