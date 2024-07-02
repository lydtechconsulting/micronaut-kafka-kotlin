package demo.integration

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.ConsumerGroupListing
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult
import org.apache.kafka.clients.admin.ConsumerGroupDescription
import org.apache.kafka.clients.admin.ListConsumerGroupsResult
import org.awaitility.Awaitility
import java.util.concurrent.TimeUnit

class KafkaTestUtils private constructor(bootstrapServers: String) {

    private val adminClient: AdminClient = AdminClient.create(mapOf(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers
    ))

    companion object {
        private var kafkaTestUtils: KafkaTestUtils? = null

        fun initialize(bootstrapServers: String): KafkaTestUtils {
            if (kafkaTestUtils == null) {
                kafkaTestUtils = KafkaTestUtils(bootstrapServers)
            }
            return kafkaTestUtils!!
        }
    }

    /**
     * Check that the given topic has the expected number of consumers.  This can be used to ensure
     * the application consumer has successfully started consuming from a topic before the test sends
     * in events.
     */
    fun waitForApplicationConsumer(topic: String) {
        Awaitility.await()
            .atMost(30, TimeUnit.SECONDS)
            .until { isConsumerListening(topic) }
    }

    private fun isConsumerListening(topic: String): Boolean {
        val groupsResult: ListConsumerGroupsResult = adminClient.listConsumerGroups()
        val groupListings: Collection<ConsumerGroupListing> = groupsResult.all().get()

        for (groupListing in groupListings) {
            val describeResult: DescribeConsumerGroupsResult = adminClient.describeConsumerGroups(
                listOf(groupListing.groupId())
            )
            val groupDescription: ConsumerGroupDescription = describeResult.all().get()[groupListing.groupId()] ?: continue

            val isListening = groupDescription.members().stream()
                .flatMap { member -> member.assignment().topicPartitions().stream() }
                .anyMatch { tp -> tp.topic() == topic }

            if (isListening) {
                return true
            }
        }
        return false
    }
}
