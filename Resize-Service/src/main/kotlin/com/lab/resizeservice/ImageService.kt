package com.lab.resizeservice

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.sql.rowset.serial.SerialBlob

@Service
@Transactional
class ImageService(
    private val imageRepository: ImageRepository,
    private val publisher: ApplicationEventPublisher
) {
    private val logger = LoggerFactory.getLogger(MessageConsumer::class.java)

    fun reduceImageQualityAndSendGenerateThumbnailCommand(imageId: Long) {
        val imageEntity = imageRepository.findByIdOrNull(imageId) ?: run {
            logger.error("Image with id $imageId not found in database")
            return
        }

        val initialImageBlob = imageEntity.initialImage ?: run {
            logger.error("No initial image found for id $imageId")
            return
        }

        val initialImage = ImageIO.read(initialImageBlob.binaryStream) ?: run {
            logger.error("Cannot decode initial image for id $imageId")
            return
        }

        val resizedImage = resizeImage(initialImage)
        logger.info("Resized image $imageId from ${initialImage.width}x${initialImage.height} to ${resizedImage.width}x${resizedImage.height}")

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(resizedImage, "png", outputStream)
        val resizedImageBlob = SerialBlob(outputStream.toByteArray())

        imageEntity.resizedImage = resizedImageBlob

        publisher.publishEvent(SendGenerateThumbnailCommand(imageId))
    }

    // resize image to fit into 1280x720 rectangle
    private fun resizeImage(image: BufferedImage): BufferedImage {
        val horizontalOverflow = image.width.toFloat() / 1280
        val verticalOverflow = image.height.toFloat() / 720

        val maxOverflow = maxOf(horizontalOverflow, verticalOverflow)
        if (maxOverflow <= 1) {
            return image
        }

        val newWidth = (image.width / maxOverflow).toInt()
        val newHeight = (image.height / maxOverflow).toInt()
        return resizeImage(image, newWidth, newHeight)
    }

    private fun resizeImage(image: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage {
        val scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT)
        val outputImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        outputImage.graphics.drawImage(scaledImage, 0, 0, null)
        return outputImage
    }
}