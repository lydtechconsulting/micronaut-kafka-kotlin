package demo.component

import demo.event.DemoInboundEvent
import dev.lydtech.component.framework.client.kafka.KafkaClient
import dev.lydtech.component.framework.extension.ComponentTestExtension
import dev.lydtech.component.framework.mapper.JsonMapper
import lombok.extern.slf4j.Slf4j
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Slf4j
@ExtendWith(ComponentTestExtension::class)
class EndToEndCT {

    private lateinit var consumer: Consumer<Any, Any>

    @BeforeEach
    fun setup() {
        consumer = KafkaClient.getInstance().initConsumer("EndToEndCT", "demo-outbound-topic", 3L)
    }

    @AfterEach
    fun tearDown() {
        consumer.close()
    }

    /**
     * Send in multiple events and ensure an outbound event is emitted for each.
     */
    @Test
    fun testFlow() {
        val totalMessages = 100
        for (i in 0 until totalMessages) {
            val id = UUID.randomUUID().toString()
            KafkaClient.getInstance().sendMessage("demo-inbound-topic", null, JsonMapper.writeToJson(DemoInboundEvent(id, "Test data")))
        }
        val outboundEvents = KafkaClient.getInstance().consumeAndAssert<String>("testFlow", consumer, totalMessages, 3)
        outboundEvents.stream().forEach { outboundEvent: ConsumerRecord<String, String> ->
            assertThat(outboundEvent.value(), containsString("Test data"))
        }
    }
}
