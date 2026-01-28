package net.kigawa.keruta.ktcp.model.task

import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerTaskCreateMsgTest {
    @Test
    fun testCreateServerTaskCreateMsg() {
        // Arrange & Act
        val msg = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            name = "test-task",
            queueId = 1
        )

        // Assert
        assertEquals(ServerMsgType.TASK_CREATE, msg.type)
        assertEquals("test-task", msg.name)
        assertEquals(1, msg.queueId)
    }

    @Test
    fun testCreateServerTaskCreateMsgWithDefaultType() {
        // Arrange & Act
        val msg = ServerTaskCreateMsg(
            name = "test-task",
            queueId = 1
        )

        // Assert
        assertEquals(ServerMsgType.TASK_CREATE, msg.type)
    }

    @Test
    fun testCreateServerTaskCreateMsgWithInvalidTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ServerTaskCreateMsg(
                type = ServerMsgType.AUTH_REQUEST,
                name = "test-task",
                queueId = 1
            )
        }
    }

    @Test
    fun testCreateServerTaskCreateMsgWithGenericErrorTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ServerTaskCreateMsg(
                type = ServerMsgType.GENERIC_ERROR,
                name = "test-task",
                queueId = 1
            )
        }
    }
}
