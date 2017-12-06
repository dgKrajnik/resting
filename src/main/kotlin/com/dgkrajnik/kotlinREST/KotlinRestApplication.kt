package com.dgkrajnik.kotlinREST

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.boot.SpringApplication
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.stereotype.Component
import javax.ws.rs.Path;
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Service
import javax.inject.Inject
import javax.ws.rs.GET

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
}

// Should definitely probably be in another file.
@Component
@Path("jaxhello")
class JAXHelloResource {
    @GET
    @Path("/string")
    fun helloString() = "Hello, JAX-RS!"

    @Inject
    lateinit var jaxHelloService: HelloService

    @GET
    @Path("/service")
    fun jaxHelloService() = jaxHelloService.helloAsAService()
}

// Commented out because Spring MVC is nicer.
/*
@Component
class HelloConfig : ResourceConfig() {
    init {
        registerEndpoints()
    }

    private fun registerEndpoints() {
        register(JAXHelloResource())
    }
}
*/

@Service
class HelloService {
    fun helloAsAService() = "Hello, Service!"
}
