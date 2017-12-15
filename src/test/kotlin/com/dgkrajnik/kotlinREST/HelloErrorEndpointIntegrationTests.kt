package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import javax.inject.Inject
import com.fasterxml.jackson.annotation.JsonProperty

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HelloErrorEndpointIntegrationTests {
    val BASE_PATH = "/hello"
    val mapper = ObjectMapper().registerModule(KotlinModule())
	@Inject
	lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun testErrorHandling() {
        val error = testRestTemplate.getForEntity("$BASE_PATH/throwAnError", ApiError::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, error.statusCode)
        assertEquals("Malformed JSON Request", error.body.message)
        assertEquals("Wink wonk", error.body.debugMessage)
    }

    @Test
    fun testCustomErrorHandling() {
        val error = testRestTemplate.getForEntity("$BASE_PATH/badRequest", ApiError::class.java)
        assertEquals(HttpStatus.NOT_FOUND, error.statusCode)
        assertEquals("Entity Not Found", error.body.message)
        assertEquals("Entity null != 22 not found.", error.body.debugMessage)
    }

    @Test
    fun testCustomVerificationErrorHandling() {
        var loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        var loginData: MultiValueMap<String, String> = LinkedMultiValueMap(mapOf(
                "reqparam" to listOf("24")
        ))
        val loginRequest = HttpEntity(loginData, loginHeaders)
        val error = testRestTemplate.postForEntity("$BASE_PATH/badPost", loginRequest, ApiError::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, error.statusCode)
        assertEquals("Error in Request Data", error.body.message)
        assertEquals(24, (error.body.subErrors?.get(0)?.returnErrorObject() as ApiValidationError).rejectedValue)
    }
}
