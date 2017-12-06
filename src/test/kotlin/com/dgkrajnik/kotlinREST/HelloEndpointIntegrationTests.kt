package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloEndpointIntegrationTests {
    val mapper = ObjectMapper().registerModule(KotlinModule())
	@Inject
	lateinit var testRestTemplate: TestRestTemplate;

	@Test
    fun testHelloController() {
        val result = testRestTemplate.getForEntity("/hello/string", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Hello, Spring!", result.body)
    }

    @Test
    fun testHelloService() {
        val result = testRestTemplate.getForEntity("/hello/service", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Hello, Service!", result.body)
    }

    @Test
    fun testHelloDTO() {
        val result = testRestTemplate.getForEntity("/hello/data", HelloData::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(HelloData("Hello, Data!"), result.body)
    }

}
