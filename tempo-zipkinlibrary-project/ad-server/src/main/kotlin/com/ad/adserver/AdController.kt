package com.ad.adserver

import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.bodyToMono


@Slf4j
@RestController
class AdController {


    @GetMapping("/ad/recommend")
    fun recommendAd(@RequestParam(required = false) age:String):AdRecommend{


//       WebClient.create()
//           .get()
//           .uri("http://naver.com")
//           .exchangeToMono {resposne-> resposne.bodyToMono<String>()}
//           .block()

        return AdRecommend("","","","")


    }
}

