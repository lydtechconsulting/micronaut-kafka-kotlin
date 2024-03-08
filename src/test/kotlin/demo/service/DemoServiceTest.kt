package demo.service

import demo.event.DemoInboundEvent
import demo.event.DemoOutboundEvent
import demo.producer.DemoProducer
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DemoServiceTest {

    private val demoProducer = mockk<DemoProducer>()
    private lateinit var demoService: DemoService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        demoService = DemoService(demoProducer)
    }

    @Test
    fun testProcess() {
        val demoInboundEvent = DemoInboundEvent("1", "Test Data")
        every { demoProducer.sendOutbound(any(DemoOutboundEvent::class)) } just Runs
        demoService.process(demoInboundEvent)
        verify(exactly = 1) { demoProducer.sendOutbound(any(DemoOutboundEvent::class)) }
    }
}
