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
class HelloEndpointIntegrationTests {
    /*
    @Before
    fun initDB() {
        val c: Connection = DriverManager.getConnection("jdbc:hsqldb:file:dbs/testDB", "SA", "")
    }
    */

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
        val result = testRestTemplate
                .withBasicAuth("steve", "userpass")
                .getForEntity("$BASE_PATH/secureData", HelloData::class.java)
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

    @Test
    fun testOAuth() {
        val loginResponse = oAuthLogin()
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)
    }

    @Test
    fun testOAuthFailure() {
        val result = testRestTemplate.getForEntity("/oauth/token", String::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    fun testHelloFailureOAuth() {
        val result = testRestTemplate.getForEntity("$BASE_PATH/secureOAuthData", HelloData::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    fun testHelloOAuth() {
        val loginResponse = oAuthLogin()
        assertNotNull(loginResponse)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        val token = loginResponse.body.accessToken
        val tokenHeaders = HttpHeaders()
        tokenHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        tokenHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer $token")
        val tokenRequest = HttpEntity(null, tokenHeaders)
        val result = testRestTemplate.exchange(
                "$BASE_PATH/secureOAuthData",
                HttpMethod.GET,
                tokenRequest,
                HelloData::class.java
        )
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(HelloData("Hello, OAuth!"), result.body)
    }

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

    private fun oAuthLogin(): ResponseEntity<OAuthResponse> {
        var loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        var loginData: MultiValueMap<String, String> = LinkedMultiValueMap(mapOf(
                "username" to listOf("neev"),
                "password" to listOf("otheruserpass"),
                "grant_type" to listOf("password")
        ))
        val loginRequest = HttpEntity(loginData, loginHeaders)
        val loginResponse = testRestTemplate
                .withBasicAuth("normalClient", "spookysecret")
                .postForEntity("/oauth/token", loginRequest, OAuthResponse::class.java)
        return loginResponse
    }

    data class OAuthResponse(
            @JsonProperty("access_token") val accessToken: String,
            @JsonProperty("token_type") val tokenType: String,
            @JsonProperty("expires_in") val expiresIn: Int,
            @JsonProperty("scope") val scope: String
    )
}
