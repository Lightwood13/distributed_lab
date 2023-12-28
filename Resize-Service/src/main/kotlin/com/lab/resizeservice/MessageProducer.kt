package com.lab.resizeservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${GENERATE_THUMBNAIL_TOPIC}") private val GENERATE_THUMBNAIL_TOPIC: String
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendMessage(event: SendGenerateThumbnailCommand) {
        kafkaTemplate.send(GENERATE_THUMBNAIL_TOPIC, event.imageId.toString(), event.imageId.toString())
    }
}

data class SendGenerateThumbnailCommand(val imageId: Long)