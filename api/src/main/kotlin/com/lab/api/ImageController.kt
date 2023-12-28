package com.lab.api

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
class ImageController(
    private val imageService: ImageService
) {

    @PostMapping("/upload")
    fun upload(@RequestParam image: MultipartFile): Long =
        imageService.saveImageAndSendReduceQualityCommand(image)

    @GetMapping("/image/{imageId}/initial", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getInitialImage(@PathVariable imageId: Long): ByteArray? =
        imageService.getInitialImage(imageId)

    @GetMapping("/image/{imageId}/resized", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getResizedImage(@PathVariable imageId: Long): ByteArray? =
        imageService.getResizedImage(imageId)

    @GetMapping("/image/{imageId}/thumbnail", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getThumbnail(@PathVariable imageId: Long): ByteArray? =
        imageService.getThumbnail(imageId)

    @GetMapping("/image/{imageId}/watermark", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getImageWithWatermark(@PathVariable imageId: Long): ByteArray? =
        imageService.getImageWithWatermark(imageId)
}