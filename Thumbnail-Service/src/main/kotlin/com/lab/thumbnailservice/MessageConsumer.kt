package com.lab.thumbnailservice

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class MessageConsumer(
    private val imageService: ImageService
) {

    private val logger = LoggerFactory.getLogger(MessageConsumer::class.java)

    @KafkaListener(topics = ["\${GENERATE_THUMBNAIL_TOPIC}"], groupId = "group-2")
    fun listen(message: String) {
        val imageId = message.toLongOrNull() ?: run {
            logger.error("Received invalid id: $message")
            return
        }
        val executionTime = measureTimeMillis {
            imageService.generateThumbnailAndSendAddWatermarkCommand(imageId)
        }
        logger.info("Processed image in $executionTime ms")

    }
}