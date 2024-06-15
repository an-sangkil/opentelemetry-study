package com.ad.adserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class AdController {


    @GetMapping("/ad/recommend")
    fun recommendAd(@RequestParam(required = false) age:String):AdRecommend{


        val recommend = AdRecommend("","","","");

        return recommend;
    }
}

