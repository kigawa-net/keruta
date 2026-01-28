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
            title = "test-task",
            queueId = 1
        )

        // Assert
        assertEquals(ServerMsgType.TASK_CREATE, msg.type)
        assertEquals("test-task", msg.title)
        assertEquals(1, msg.queueId)
    }

    @Test
    fun testCreateServerTaskCreateMsgWithDefaultType() {
        // Arrange & Act
        val msg = ServerTaskCreateMsg(
            title = "test-task",
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
                title = "test-task",
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
                title = "test-task",
                queueId = 1
            )
        }
    }
}
