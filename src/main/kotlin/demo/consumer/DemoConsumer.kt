package demo.consumer

import demo.event.DemoInboundEvent
import demo.service.DemoService
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@KafkaListener(groupId = "demo-group-id")
class DemoConsumer(private val demoService: DemoService) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Topic("demo-inbound-topic")
    fun receive(demoInboundEvent: DemoInboundEvent) {
        log.info("Received message with id: " + demoInboundEvent.id)
        try {
            demoService.process(demoInboundEvent)
        } catch (e: Exception) {
            log.error("Error processing message: " + e.message)
        }
    }
}
