package com.lab.thumbnailservice

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

    fun generateThumbnailAndSendAddWatermarkCommand(imageId: Long) {
        val imageEntity = imageRepository.findByIdOrNull(imageId) ?: run {
            logger.error("Image with id $imageId not found in database")
            return
        }

        val resizedImageBlob = imageEntity.resizedImage ?: run {
            logger.error("No initial image found for id $imageId")
            return
        }

        val resizedImage = ImageIO.read(resizedImageBlob.binaryStream) ?: run {
            logger.error("Cannot decode initial image for id $imageId")
            return
        }

        val thumbnail = resizeImage(resizedImage, 320, 240)
        logger.info("Resized image $imageId rom ${resizedImage.width}x${resizedImage.height} to ${thumbnail.width}x${thumbnail.height}")

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(thumbnail, "png", outputStream)
        val thumbnailBlob = SerialBlob(outputStream.toByteArray())

        imageEntity.thumbnail = thumbnailBlob

        publisher.publishEvent(SendAddWatermarkCommand(imageId))
    }

    private fun resizeImage(image: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage {
        val scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT)
        val outputImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        outputImage.graphics.drawImage(scaledImage, 0, 0, null)
        return outputImage
    }
}