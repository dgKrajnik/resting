package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

/**
 * A generic class for returning errors RESTfully.
 */
data class ApiError (
    val status: HttpStatus?,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val timestamp: LocalDateTime?,
    val message: String?,
    @JsonProperty("debug_message") val debugMessage: String?,
    @JsonProperty("sub_errors") val subErrors: List<ApiSubError>?
)

class ApiErrorBuilder {
    var status: HttpStatus? = null
    var timestamp: LocalDateTime? = null
    var message: String? = null
    var debugMessage: String? = null
    var subErrors: MutableList<ApiSubError>? = null

    constructor(hs: HttpStatus, message: String, ex: Exception) {
        this.status = hs
        this.message = message
        this.debugMessage = ex.localizedMessage
    }

    fun addSubError(subError: ApiSubError) {
        if (subErrors == null) {
            this.subErrors = arrayListOf(subError)
        } else {
            this.subErrors?.add(subError)
        }
    }

    fun build(): ApiError {
        return ApiError(status, timestamp, message, debugMessage, subErrors)
    }
}

@JsonDeserialize(using = ApiSubErrorDeserializer::class)
interface ApiSubError {
    // I can't use `get` because Kotlin thinks it's clever.
    fun returnErrorObject(): Any
}

/**
 * An ApiSubError for when submitted data fails to validate correctly.
 */
data class ApiValidationError(val resource: String, val field: String, val rejectedValue: Any, val message: String): ApiSubError {
    override fun returnErrorObject(): Any {
        return this
    }
}

class ApiSubErrorDeserializer(vc: Class<Any>?) : StdDeserializer<ApiSubError>(vc) {
    constructor() : this(null)

    override fun deserialize(parser: JsonParser, context: DeserializationContext) : ApiSubError {
        val node: JsonNode = parser.codec.readTree(parser)
        if (node.has("rejectedValue")) {
            return ApiValidationError(
                    node.get("resource").asText(),
                    node.get("field").asText(),
                    node.get("rejectedValue").numberValue() ?: node.get("rejectedValue").asText(),
                    node.get("message").asText()
            )
        }
        throw RuntimeException()
    }
}

/**
 * Exception thrown when a requested entity doesn't exist.
 */
class EntityNotFoundException(message: String) : Exception(message)
class ValidationFailedException(val resource: String, val field: String, val rejectedValue: Any, val hint: String?) : Exception(hint) {
    fun asApiValidationError(): ApiValidationError {
        val message = hint ?: "No advice"
        return ApiValidationError(this.resource, field, rejectedValue, message)
    }
}
