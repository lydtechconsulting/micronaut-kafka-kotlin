package demo

import io.micronaut.runtime.Micronaut

object DemoApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(DemoApplication::class.java)
    }
}
