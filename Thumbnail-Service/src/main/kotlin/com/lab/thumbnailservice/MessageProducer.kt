package com.lab.thumbnailservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${ADD_WATERMARK_TOPIC}") private val ADD_WATERMARK_TOPIC: String
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendMessage(event: SendAddWatermarkCommand) {
        kafkaTemplate.send(ADD_WATERMARK_TOPIC, event.imageId.toString(), event.imageId.toString())
    }
}

data class SendAddWatermarkCommand(val imageId: Long)