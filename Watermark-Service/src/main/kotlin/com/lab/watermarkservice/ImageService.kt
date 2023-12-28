package com.lab.watermarkservice

import net.coobird.thumbnailator.filters.Caption
import net.coobird.thumbnailator.geometry.Position
import net.coobird.thumbnailator.geometry.Positions
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.awt.Color
import java.awt.Font
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

    fun addWatermark(imageId: Long) {
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

        val watermarkedImage = addWatermark(resizedImage)
        logger.info("Added watermark to image $imageId")

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(watermarkedImage, "png", outputStream)
        val watermarkedImageBlob = SerialBlob(outputStream.toByteArray())

        imageEntity.imageWithWatermark = watermarkedImageBlob
    }

    private fun addWatermark(image: BufferedImage): BufferedImage {
        val caption = "Distributed lab"
        val font = Font("Monospaced", Font.PLAIN, 14)
        val c = Color.black
        val position: Position = Positions.CENTER
        val insetPixels = 0

        val filter = Caption(caption, font, c, position, insetPixels)

        return filter.apply(image)
    }
}