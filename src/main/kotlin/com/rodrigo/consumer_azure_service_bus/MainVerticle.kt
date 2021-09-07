package com.rodrigo.consumer_azure_service_bus

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusErrorContext
import com.azure.messaging.servicebus.ServiceBusException
import com.azure.messaging.servicebus.ServiceBusProcessorClient
import com.azure.messaging.servicebus.ServiceBusReceivedMessage
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext
import io.vertx.core.AbstractVerticle
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


class MainVerticle : AbstractVerticle() {

    override fun start() {
        val onMessage: Consumer<ServiceBusReceivedMessageContext> =
            Consumer<ServiceBusReceivedMessageContext> { context ->
                val message: ServiceBusReceivedMessage = context.message
                println("Processing message. Sequence #: ${message.sequenceNumber}. Contents: ${message.body}")
            }

        val onError =
            Consumer { context: ServiceBusErrorContext ->
                println("Error when receiving messages from namespace: ${context.fullyQualifiedNamespace}. Entity: ${context.entityPath}")
                if (context.exception is ServiceBusException) {
                    val exception = context.exception as ServiceBusException
                    println("Error source: ${context.errorSource}, reason ${exception.reason}")
                } else {
                    println("Error occurred: ${context.exception}")
                }
            }

        // Create an instance of the processor through the ServiceBusClientBuilder
        val processorClient: ServiceBusProcessorClient = ServiceBusClientBuilder()
            .connectionString("Endpoint=sb://<HOST_ENDPOINT>;SharedAccessKeyName=<ACCESS_KEY_NAME>;SharedAccessKey=<ACCESS_KEY>")
            .processor()
            .queueName("<QUEUE_NAME>")
            .processMessage(onMessage)
            .processError(onError)
            .buildProcessorClient()

        println("Starting the processor")
        processorClient.start()

        TimeUnit.SECONDS.sleep(10)
        println("Stopping and closing the processor")
        processorClient.close()

        super.start()
    }
}
