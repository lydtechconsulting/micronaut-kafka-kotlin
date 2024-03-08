package demo.service

import org.slf4j.LoggerFactory
import demo.event.DemoInboundEvent
import demo.event.DemoOutboundEvent
import demo.producer.DemoProducer
import jakarta.inject.Singleton
import java.util.UUID.randomUUID

@Singleton
class DemoService(private val demoProducer: DemoProducer) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun process(demoInboundEvent: DemoInboundEvent) {
        val demoOutboundEvent = DemoOutboundEvent(randomUUID().toString(), "Processed data: " + demoInboundEvent.data)
        demoProducer.sendOutbound(demoOutboundEvent)
        log.info("Sent outbound event for consumed event with id: {}", demoInboundEvent.id)
    }
}
