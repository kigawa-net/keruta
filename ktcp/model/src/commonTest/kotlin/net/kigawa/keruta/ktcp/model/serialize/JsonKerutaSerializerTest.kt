package net.kigawa.keruta.ktcp.model.serialize

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.kigawa.keruta.ktcp.model.err.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.kodel.api.err.Res
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JsonKerutaSerializerTest {
    private val serializer = JsonKerutaSerializer()

    @Test
    fun testSerializeServerTaskCreateMsg() {
        // Arrange
        val msg = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            name = "test-task",
            queueId = 1
        )

        // Act
        val json = serializer.serialize(ServerTaskCreateMsg.serializer(), msg)

        // Assert
        assertTrue(json.contains("\"name\":\"test-task\""))
        assertTrue(json.contains("\"queueId\":\"queue-123\""))
        assertTrue(json.contains("\"type\":\"task_create\""))
    }

    @Test
    fun testDeserializeServerTaskCreateMsg() {
        // Arrange
        val json = """{"type":"task_create","name":"test-task","queueId":"queue-123"}"""

        // Act
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), json)

        // Assert
        when (result) {
            is Res.Ok -> {
                val msg = result.value
                assertEquals(ServerMsgType.TASK_CREATE, msg.type)
                assertEquals("test-task", msg.name)
                assertEquals(1, msg.queueId)
            }

            is Res.Err -> kotlin.test.fail("Expected Ok but got Err: ${result.err}")
        }
    }

    @Test
    fun testDeserializeInvalidJson() {
        // Arrange
        val invalidJson = """{"type":"task_create","name":"""

        // Act
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), invalidJson)

        // Assert
        when (result) {
            is Res.Ok -> kotlin.test.fail("Expected Err but got Ok")
            is Res.Err -> assertIs<IllegalFormatDeserializeErr>(result.err)
        }
    }

    @Test
    fun testDeserializeMissingRequiredField() {
        // Arrange - JSONに必須フィールド（name）が欠けている
        val jsonWithMissingField = """{"type":"task_create","queueId":"queue-123"}"""

        // Act
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), jsonWithMissingField)

        // Assert
        when (result) {
            is Res.Ok -> kotlin.test.fail("Expected Err but got Ok")
            is Res.Err -> assertIs<IllegalFormatDeserializeErr>(result.err)
        }
    }

    @Test
    fun testSerializeWithDefaults() {
        @Serializable
        data class TestData(val value: String, val optional: String = "default")

        // Arrange
        val data = TestData(value = "test")

        // Act
        val json = serializer.serialize(serializer.serializersModule.serializer(), data)

        // Assert - encodeDefaults = true なので、デフォルト値も含まれる
        assertTrue(json.contains("\"optional\":\"default\""))
    }

    @Test
    fun testRoundTripSerialization() {
        // Arrange
        val original = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            name = "round-trip-task",
            queueId = 1
        )

        // Act
        val json = serializer.serialize(ServerTaskCreateMsg.serializer(), original)
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), json)

        // Assert
        when (result) {
            is Res.Ok -> {
                val deserialized = result.value
                assertEquals(original.type, deserialized.type)
                assertEquals(original.name, deserialized.name)
                assertEquals(original.queueId, deserialized.queueId)
            }

            is Res.Err -> kotlin.test.fail("Expected Ok but got Err: ${result.err}")
        }
    }

    @Test
    fun testInlineSerializeExtension() {
        // Arrange
        val msg = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            name = "inline-test",
            queueId = 1
        )

        // Act
        val json = serializer.serialize(msg)

        // Assert
        assertTrue(json.contains("\"name\":\"inline-test\""))
    }

    @Test
    fun testInlineDeserializeExtension() {
        // Arrange
        val json = """{"type":"task_create","name":"inline-test","queueId":"queue-789"}"""

        // Act
        val result: Res<ServerTaskCreateMsg, *> = serializer.deserialize(json)

        // Assert
        when (result) {
            is Res.Ok -> assertEquals("inline-test", result.value.name)
            is Res.Err -> kotlin.test.fail("Expected Ok but got Err: ${result.err}")
        }
    }
}
