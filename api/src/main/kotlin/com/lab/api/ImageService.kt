package com.lab.api

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.sql.rowset.serial.SerialBlob

@Service
@Transactional
class ImageService(
    private val imageRepository: ImageRepository,
    private val publisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    fun saveImageAndSendReduceQualityCommand(imageFile: MultipartFile): Long {
        // read image
        val image = ImageIO.read(ByteArrayInputStream(imageFile.bytes)) ?: throw ImageFormatNotSupported()
        logger.info("Received image ${imageFile.originalFilename} with size: ${image.width}x${image.height}")

        // convert to png
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)

        // save to database
        val imageEntity = Image(initialImage = SerialBlob(outputStream.toByteArray()))
        val imageId = imageRepository.save(imageEntity).id!!

        // send command to kafka to reduce image quality
        publisher.publishEvent(SendReduceQualityCommand(imageId))

        return imageId
    }

    fun getInitialImage(imageId: Long): ByteArray? {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw ImageNotFound()

        return image.initialImage?.binaryStream?.readBytes()
    }

    fun getResizedImage(imageId: Long): ByteArray? {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw ImageNotFound()

        return image.resizedImage?.binaryStream?.readBytes()
    }

    fun getThumbnail(imageId: Long): ByteArray? {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw ImageNotFound()

        return image.thumbnail?.binaryStream?.readBytes()
    }

    fun getImageWithWatermark(imageId: Long): ByteArray? {
        val image = imageRepository.findByIdOrNull(imageId) ?: throw ImageNotFound()

        return image.imageWithWatermark?.binaryStream?.readBytes()
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Image format not supported")
class ImageFormatNotSupported : Exception()

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Image not found")
class ImageNotFound : Exception()