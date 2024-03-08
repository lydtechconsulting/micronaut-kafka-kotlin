package demo.event

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DemoOutboundEvent(val id: String, val data: String)
