package com.dgkrajnik.kotlinREST

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException as SpringAccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val error = "Malformed JSON Request"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.BAD_REQUEST, error, ex).build())
    }

    @ExceptionHandler(EntityNotFoundException::class)
    protected fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any> {
        val error = "Entity Not Found"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.NOT_FOUND, error, ex).build())
    }

    @ExceptionHandler(ValidationFailedException::class)
    protected fun handleValidationFailed(ex: ValidationFailedException): ResponseEntity<Any> {
        val error = "Error in Request Data"
        val apiError = ApiErrorBuilder(HttpStatus.BAD_REQUEST, error , ex)
        apiError.addSubError(ex.asApiValidationError())
        return buildResponseEntity(apiError.build())
    }

    /* This handler doesn't actually get called because Spring Security filters handle errors higher-up
    @ExceptionHandler(SpringAccessDeniedException::class)
    protected fun handleAccessDenied(ex: SpringAccessDeniedException): ResponseEntity<Any> {
        val error = "Access Denied to This Resource"
        return buildResponseEntity(ApiErrorBuilder(HttpStatus.UNAUTHORIZED, error, ex).build())
    }
    */

    private fun buildResponseEntity(apiError: ApiError): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        return ResponseEntity(apiError, headers, apiError.status)
    }
}
