package demo.consumer

import demo.event.DemoInboundEvent
import demo.service.DemoService
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DemoConsumerTest {

    private val serviceMock = mockk<DemoService>()
    private lateinit var consumer: DemoConsumer

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        consumer = DemoConsumer(serviceMock)
    }

    @Test
    fun testReceive() {
        val demoInboundEvent = DemoInboundEvent("1", "Test Data")
        every { serviceMock.process(any(DemoInboundEvent::class)) } just Runs

        consumer.receive(demoInboundEvent)

        verify(exactly = 1) { serviceMock.process(demoInboundEvent) }
    }

    @Test
    fun testReceive_Exception() {
        val demoInboundEvent = DemoInboundEvent("1", "Test Data")
        every { serviceMock.process(any()) } throws RuntimeException("Exception thrown")

        consumer.receive(demoInboundEvent)

        verify(exactly = 1) { serviceMock.process(demoInboundEvent) }
    }
}
