package com.lab.watermarkservice

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MessageConsumer(
    private val imageService: ImageService
) {

    private val logger = LoggerFactory.getLogger(MessageConsumer::class.java)

    @KafkaListener(topics = ["\${ADD_WATERMARK_TOPIC}"], groupId = "group-3")
    fun listen(message: String) {
        val imageId = message.toLongOrNull() ?: run {
            logger.error("Received invalid id: $message")
            return
        }
        imageService.addWatermark(imageId)
    }
}