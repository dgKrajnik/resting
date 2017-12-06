package com.dgkrajnik.kotlinREST

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class KotlinRestApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinRestApplication::class.java, *args)
}
