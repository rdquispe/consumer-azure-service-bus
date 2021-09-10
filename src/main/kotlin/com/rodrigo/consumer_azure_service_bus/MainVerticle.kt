package com.rodrigo.consumer_azure_service_bus

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusErrorContext
import com.azure.messaging.servicebus.ServiceBusException
import com.azure.messaging.servicebus.ServiceBusProcessorClient
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext
import io.vertx.core.AbstractVerticle
import java.util.concurrent.TimeUnit


class MainVerticle : AbstractVerticle() {

    companion object {
        const val CONNECTION_STRING: String =
            "Endpoint=sb://<HOST_ENDPOINT>;SharedAccessKeyName=<ACCESS_KEY_NAME>;SharedAccessKey=<ACCESS_KEY>"
        const val QUEUE_NAME: String = "<QUEUE_NAME>"
    }

    override fun start() {
        val processorClient: ServiceBusProcessorClient = ServiceBusClientBuilder()
            .connectionString(CONNECTION_STRING)
            .processor()
            .queueName(QUEUE_NAME)
            .processMessage { receivedMessageContext -> onMessage(receivedMessageContext) }
            .processError { errorContext -> onError(errorContext) }
            .buildProcessorClient()

        println("Starting the processor")
        processorClient.start()

        TimeUnit.SECONDS.sleep(10)
        println("Stopping and closing the processor")
        processorClient.close()

        super.start()
    }

    private fun onMessage(context: ServiceBusReceivedMessageContext) {
        println("Processing message. Sequence #: ${context.message.sequenceNumber}. Contents: ${context.message.body}")
    }

    private fun onError(context: ServiceBusErrorContext) {
        if (context.exception is ServiceBusException) {
            val exception = context.exception as ServiceBusException
            println("Error source: ${context.errorSource}, reason ${exception.reason}")
        } else {
            println("Error occurred: ${context.exception}")
        }
    }
}
