package com.dgkrajnik.kotlinREST

import org.springframework.boot.SpringApplication
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import javax.inject.Inject

@SpringBootApplication
class KotlinRestApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinRestApplication::class.java, *args)
}

// Should probably be in another file.
@RestController
@RequestMapping("hello")
class SpringHelloController {
    @GetMapping("/string")
    fun helloString() = "Hello, Spring!"

    @Inject //Inject is hip and modern.
    lateinit var springHelloService: HelloService

    @GetMapping("/service")
    fun helloService() = springHelloService.helloAsAService()

    @GetMapping("/secureData")
    fun helloData() = HelloData("Hello, Data!")

    @GetMapping("/secureOAuthData")
    fun helloOAuthData() = HelloData("Hello, OAuth!")
}

@Service
class HelloService {
    fun helloAsAService() = "Hello, Service!"
}

data class HelloData(val message: String)
