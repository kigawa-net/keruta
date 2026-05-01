package net.kigawa.keruta.ktcl.mobile.queue

import kotlin.test.Test
import kotlin.test.assertEquals
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue

class QueueRepositoryTest {

    @Test
    fun testQueueDataClass() {
        val queue = Queue(id = 1, name = "Test Queue")
        
        assertEquals(1, queue.id)
        assertEquals("Test Queue", queue.name)
    }

    @Test
    fun testMultipleQueues() {
        val queues = listOf(
            Queue(id = 1, name = "Queue 1"),
            Queue(id = 2, name = "Queue 2"),
            Queue(id = 3, name = "Queue 3")
        )
        
        assertEquals(3, queues.size)
        assertEquals("Queue 1", queues[0].name)
        assertEquals("Queue 2", queues[1].name)
        assertEquals("Queue 3", queues[2].name)
    }
    
    @Test
    fun testQueueEquality() {
        val queue1 = Queue(id = 1, name = "Test")
        val queue2 = Queue(id = 1, name = "Test")
        
        assertEquals(queue1, queue2)
    }
}
