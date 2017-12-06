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
import java.net.HttpCookie
import java.net.URI
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloEndpointIntegrationTests {
    val BASE_PATH = "/hello"
    val mapper = ObjectMapper().registerModule(KotlinModule())
	@Inject
	lateinit var testRestTemplate: TestRestTemplate

	@Test
    fun testHelloController() {
        val result = testRestTemplate.getForEntity("$BASE_PATH/string", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Hello, Spring!", result.body)
    }

    @Test
    fun testHelloService() {
        val result = testRestTemplate.getForEntity("$BASE_PATH/service", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Hello, Service!", result.body)
    }

    @Test
    fun testHelloDTO() {
        // Login and get any cookies we might need to maintain session.
        var loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        var loginData: MultiValueMap<String, String> = LinkedMultiValueMap(mapOf(
                "username" to listOf("steve"),
                "password" to listOf("userpass")
        ))
        val loginRequest = HttpEntity(loginData, loginHeaders)
        val loginResponse = testRestTemplate.postForEntity("/login", loginRequest, String::class.java)
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        // Parse the cookie headers.
        val responseCookies: ArrayList<HttpCookie> = ArrayList()
        for (cookieHeader in loginResponse.headers.get(HttpHeaders.SET_COOKIE) ?: listOf()) {
            responseCookies.addAll(HttpCookie.parse(cookieHeader))
        }
        for (cookieHeader in loginResponse.headers.get(HttpHeaders.SET_COOKIE2) ?: listOf()) {
            responseCookies.addAll(HttpCookie.parse(cookieHeader))
        }

        // Reconstruct the cookies into a COOKIE: header.
        val cookieResendString = responseCookies.joinToString("; ")

        // Construct a request that includes the cookie header.
        val requestHeaders = HttpHeaders()
        requestHeaders.add(HttpHeaders.COOKIE, cookieResendString)
        val requestEntity: RequestEntity<String> = RequestEntity(
                requestHeaders,
                HttpMethod.GET,
                URI("")
        )

        val result = testRestTemplate.exchange("$BASE_PATH/secureData", HttpMethod.GET, requestEntity, HelloData::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(HelloData("Hello, Data!"), result.body)
    }

    @Test
    fun testHelloDTOFailure() {
        val authTestRestTemplate = testRestTemplate.withBasicAuth("steve", "wrongpass")
        val result = authTestRestTemplate.getForEntity("$BASE_PATH/secureData", HelloData::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }
}
