package com.dgkrajnik.kotlinREST

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(MockitoJUnitRunner::class)
class HelloControllerUnitTest {
    @InjectMocks
    lateinit var helloController: SpringHelloController

    @Mock
    lateinit var helloService: HelloService

    @Test
    fun testSpringHelloController() {
        val result = helloController.helloString()
        assertNotNull(result)
        assertEquals(result, "Hello, Spring!")
    }

    @Test
    fun testSpringHelloService() {
        doReturn("Hello, Service!").`when`(helloService).helloAsAService()
        val result = helloController.helloService()
        assertNotNull(result)
        assertEquals("Hello, Service!", result)
    }

    @Test
    fun testSpringHelloDTO() {
        val result = helloController.helloData()
        assertNotNull(result)
        assertEquals(HelloData("Hello, Data!"), result)
    }
}
