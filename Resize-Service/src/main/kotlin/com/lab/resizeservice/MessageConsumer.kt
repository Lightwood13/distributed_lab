package com.lab.resizeservice

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class MessageConsumer(
    private val imageService: ImageService
) {

    private val logger = LoggerFactory.getLogger(MessageConsumer::class.java)

    @KafkaListener(topics = ["\${REDUCE_IMAGE_QUALITY_TOPIC}"], groupId = "group-1")
    fun listen(message: String) {
        val imageId = message.toLongOrNull() ?: run {
            logger.error("Received invalid id: $message")
            return
        }
        val executionTime = measureTimeMillis {
            imageService.reduceImageQualityAndSendGenerateThumbnailCommand(imageId)
        }
        logger.info("Processed image in $executionTime ms")
    }
}