package net.kigawa.keruta.ktcp.usecase

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.kigawa.keruta.ktcp.domain.err.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.domain.serialize.deserialize
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateMsg
import net.kigawa.kodel.api.err.Res
import kotlin.test.*

class JsonKerutaSerializerTest {
    private val serializer = JsonKerutaSerializer()

    @Test
    fun testSerializeServerTaskCreateMsg() {
        // Arrange
        val msg = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            title = "test-task",
            description = "test description",
            queueId = 123
        )

        // Act
        val json = serializer.serialize(ServerTaskCreateMsg.serializer(), msg)

        // Assert
        assertTrue(json.contains("\"title\":\"test-task\""))
        assertTrue(json.contains("\"description\":\"test description\""))
        assertTrue(json.contains("\"queueId\":123"))
        assertTrue(json.contains("\"type\":\"task_create\""))
    }

    @Test
    fun testDeserializeServerTaskCreateMsg() {
        // Arrange
        val json = """{"type":"task_create","title":"test-task","description":"test description","queueId":123}"""

        // Act
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), json)

        // Assert
        when (result) {
            is Res.Ok -> {
                val msg = result.value
                assertEquals(ServerMsgType.TASK_CREATE, msg.type)
                assertEquals("test-task", msg.title)
                assertEquals(123L, msg.queueId)
            }

            is Res.Err -> fail("Expected Ok but got Err: ${result.err}")
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
            is Res.Ok -> fail("Expected Err but got Ok")
            is Res.Err -> assertIs<IllegalFormatDeserializeErr>(result.err)
        }
    }

    @Test
    fun testDeserializeMissingRequiredField() {
        // Arrange - JSONに必須フィールド（title）が欠けている
        val jsonWithMissingField = """{"type":"task_create","description":"test","queueId":123}"""

        // Act
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), jsonWithMissingField)

        // Assert
        when (result) {
            is Res.Ok -> fail("Expected Err but got Ok")
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
            title = "round-trip-task",
            description = "round trip description",
            queueId = 456L
        )

        // Act
        val json = serializer.serialize(ServerTaskCreateMsg.serializer(), original)
        val result = serializer.deserialize(ServerTaskCreateMsg.serializer(), json)

        // Assert
        when (result) {
            is Res.Ok -> {
                val deserialized = result.value
                assertEquals(original.type, deserialized.type)
                assertEquals(original.title, deserialized.title)
                assertEquals(original.queueId, deserialized.queueId)
            }

            is Res.Err -> fail("Expected Ok but got Err: ${result.err}")
        }
    }

    @Test
    fun testInlineSerializeExtension() {
        // Arrange
        val msg = ServerTaskCreateMsg(
            type = ServerMsgType.TASK_CREATE,
            title = "inline-test",
            description = "inline test description",
            queueId = 789L
        )

        // Act
        val json = serializer.serialize(msg)

        // Assert
        assertTrue(json.contains("\"title\":\"inline-test\""))
        assertTrue(json.contains("\"description\":\"inline test description\""))
    }

    @Test
    fun testInlineDeserializeExtension() {
        // Arrange
        val json = """{"type":"task_create","title":"inline-test","description":"inline test description","queueId":789}"""

        // Act
        val result: Res<ServerTaskCreateMsg, *> = serializer.deserialize(json)

        // Assert
        when (result) {
            is Res.Ok -> {
                assertEquals("inline-test", result.value.title)
                assertEquals(789L, result.value.queueId)
            }

            is Res.Err -> fail("Expected Ok but got Err: ${result.err}")
        }
    }
}
