package demo.event

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Deserializable
data class DemoInboundEvent(val id: String, val data: String)
