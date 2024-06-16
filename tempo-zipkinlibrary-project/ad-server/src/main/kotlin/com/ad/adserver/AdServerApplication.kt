package com.ad.adserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AdServerApplication

fun main(args: Array<String>) {
    runApplication<AdServerApplication>(*args)
}
