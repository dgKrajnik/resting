package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

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

    val logger: Logger = LoggerFactory.getLogger("HelloLogger")

    @GetMapping("/string")
    fun helloString() = "Hello, Spring!"

    @GetMapping("/service")
    fun helloService() = springHelloService.helloAsAService()

    @GetMapping("/secureData")
    fun helloData(): HelloData {
        logger.info("User Accessed secureData.")
        return HelloData("Hello, Data!")
    }

    @GetMapping("/secureOAuthData")
    fun helloOAuthData(): HelloData {
        logger.info("User accessed secureOAuthData.")
        return HelloData("Hello, OAuth!")
    }

    @GetMapping("/throwAnError")
    fun badBoy(): Nothing = throw HttpMessageNotReadableException("Wink wonk")

    @GetMapping("/badRequest")
    fun badReq(@RequestParam("reqparam", required=false) requestParam: Int?): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        if (requestParam != 22) {
            throw EntityNotFoundException("Entity $requestParam != 22 not found.")
        } else {
            return ResponseEntity(object{val jsonshim: String = "Good Stuff"}, headers, HttpStatus.OK)
        }
    }

    @PostMapping("/badPost")
    fun badPost(@RequestParam("reqparam", required=false) requestParam: Int): ResponseEntity<Any>{
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        if (requestParam > 22) {
            throw ValidationFailedException("request", "reqparam", requestParam, "Value must be <= 22")
        } else {
            return ResponseEntity(object{val jsonshim: String = "I got $requestParam"}, headers, HttpStatus.OK)
        }
    }

    @GetMapping("/loggedEndpoint", produces=["text/html"])
    fun iAmWatchingYou(request: HttpServletRequest): String {
        logger.info("User ${request.remoteAddr} accessed ${request.requestURL}.")
        return "<div style=\"position:absolute; top:50%; text-align:center; width:100%; transform:translateY(-50%);\">I'm watching you.</div>"
    }
}

@Service
class HelloService {
    fun helloAsAService() = "Hello, Service!"
}

data class HelloData(val message: String)
