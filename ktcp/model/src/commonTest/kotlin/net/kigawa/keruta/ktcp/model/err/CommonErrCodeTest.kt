package net.kigawa.keruta.ktcp.model.err

import kotlin.test.Test
import kotlin.test.assertEquals

class CommonErrCodeTest {
    @Test
    fun testCommonErrCodeValues() {
        // Assert - すべてのエラーコードが存在することを確認
        assertEquals(CommonErrCode.ENTRYPOINT_NOT_FOUND, CommonErrCode.valueOf("ENTRYPOINT_NOT_FOUND"))
        assertEquals(CommonErrCode.ILLEGAL_FORMAT_DESERIALIZE, CommonErrCode.valueOf("ILLEGAL_FORMAT_DESERIALIZE"))
        assertEquals(CommonErrCode.INVALID_TYPE_DESERIALIZE, CommonErrCode.valueOf("INVALID_TYPE_DESERIALIZE"))
    }

    @Test
    fun testCommonErrCodeEntriesCount() {
        // Assert - エラーコードの数を確認
        assertEquals(3, CommonErrCode.entries.size)
    }

    @Test
    fun testCommonErrCodeNames() {
        // Assert - すべてのエラーコードの名前を確認
        val entries = CommonErrCode.entries
        assertEquals("ENTRYPOINT_NOT_FOUND", entries[0].name)
        assertEquals("ILLEGAL_FORMAT_DESERIALIZE", entries[1].name)
        assertEquals("INVALID_TYPE_DESERIALIZE", entries[2].name)
    }
}