package demo.integration

import demo.event.DemoInboundEvent
import dev.lydtech.component.framework.mapper.JsonMapper
import io.micronaut.core.annotation.NonNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import net.mguenther.kafka.junit.EmbeddedKafkaCluster
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig
import net.mguenther.kafka.junit.ObserveKeyValues
import net.mguenther.kafka.junit.SendValues
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EndToEndIntegrationTest : TestPropertyProvider {

    companion object {
        private const val DEMO_INBOUND_TEST_TOPIC = "demo-inbound-topic"
        private const val DEMO_OUTBOUND_TEST_TOPIC = "demo-outbound-topic"
        private var kafka: EmbeddedKafkaCluster

        init {
            kafka = EmbeddedKafkaCluster.provisionWith(EmbeddedKafkaClusterConfig.defaultClusterConfig())
            kafka.start()
        }
    }

    @NonNull
    override fun getProperties(): Map<String, String> {
        return mapOf(
            "kafka.bootstrap.servers" to kafka.brokerList
        )
    }

    @BeforeAll
    fun setupOnce() {
        KafkaTestUtils.initialize(kafka.brokerList).waitForApplicationConsumer(DEMO_INBOUND_TEST_TOPIC)
    }

    @AfterAll
    fun tearDownOnce() {
        kafka.stop()
    }

    @Test
    fun testSuccess() {
        val totalMessages = 5
        for (i in 0 until totalMessages) {
            val id = UUID.randomUUID().toString()
            val payload = "payload-" + UUID.randomUUID()
            val event = JsonMapper.writeToJson(DemoInboundEvent(id, payload))
            kafka.send(SendValues.to(DEMO_INBOUND_TEST_TOPIC, event))
        }
        kafka.observe(ObserveKeyValues.on(DEMO_OUTBOUND_TEST_TOPIC, totalMessages))
    }
}
