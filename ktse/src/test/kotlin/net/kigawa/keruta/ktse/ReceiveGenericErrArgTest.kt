package net.kigawa.keruta.ktse

import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import kotlin.test.Test
import kotlin.test.assertTrue

class ReceiveGenericErrArgTest {

    @Test
    fun testReceiveGenericErrArgImplementsGenericErrArg() {
        val errArg = ReceiveGenericErrArg()

        // Verify that ReceiveGenericErrArg implements GenericErrArg interface
        assertTrue(errArg is GenericErrArg)
    }

    @Test
    fun testReceiveGenericErrArgCanBeInstantiated() {
        val errArg = ReceiveGenericErrArg()

        // Verify that the object can be created without issues
        assertTrue(errArg != null)
    }
}