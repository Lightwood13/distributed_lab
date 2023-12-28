package com.lab.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${REDUCE_IMAGE_QUALITY_TOPIC}") private val REDUCE_IMAGE_QUALITY_TOPIC: String
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendReduceQualityCommand(event: SendReduceQualityCommand) {
        kafkaTemplate.send(REDUCE_IMAGE_QUALITY_TOPIC, event.imageId.toString(), event.imageId.toString())
    }
}

data class SendReduceQualityCommand(val imageId: Long)