package com.lab.watermarkservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WatermarkServiceApplication

fun main(args: Array<String>) {
    runApplication<WatermarkServiceApplication>(*args)
}
