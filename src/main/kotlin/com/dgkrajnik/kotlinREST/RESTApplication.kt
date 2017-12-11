package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import javax.inject.Inject

@SpringBootApplication
class KotlinRestApplication

fun main(args: Array<String>) {
    var mapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd hh:mm:ss")) // @JsonFormat doesn't deserialise right.
    SpringApplication.run(KotlinRestApplication::class.java, *args)
}

// Should probably be in another file.
@RestController
@RequestMapping("hello")
class SpringHelloController {
    @Inject //Inject is hip and modern.
    lateinit var springHelloService: HelloService


    @GetMapping("/string")
    fun helloString() = "Hello, Spring!"

    @GetMapping("/service")
    fun helloService() = springHelloService.helloAsAService()

    @GetMapping("/secureData")
    fun helloData() = HelloData("Hello, Data!")

    @GetMapping("/secureOAuthData")
    fun helloOAuthData() = HelloData("Hello, OAuth!")

    @GetMapping("/throwAnError")
    fun badBoy(): Nothing = throw HttpMessageNotReadableException("Wink wonk")
}

@Service
class HelloService {
    fun helloAsAService() = "Hello, Service!"
}

data class HelloData(val message: String)
