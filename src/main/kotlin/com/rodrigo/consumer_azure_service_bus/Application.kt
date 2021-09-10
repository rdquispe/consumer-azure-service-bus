package com.rodrigo.consumer_azure_service_bus

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

class Application {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val vertex = Vertx.vertx()
            val mainVerticle = MainVerticle()

            vertex.deployVerticle(mainVerticle, DeploymentOptions().apply {})

            println("Application Success Running")
        }
    }
}
