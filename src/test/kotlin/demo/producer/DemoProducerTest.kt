package demo.producer

import demo.event.DemoOutboundEvent
import org.junit.jupiter.api.Test

class DemoProducerTest {

    @Test
    fun testSendOutbound() {
        val demoProducer: DemoProducer = object : DemoProducer {
            override fun sendOutbound(demoOutboundEvent: DemoOutboundEvent) {}
        }
        val demoOutboundEvent = DemoOutboundEvent("1", "Test Data")
        demoProducer.sendOutbound(demoOutboundEvent)
    }
}
