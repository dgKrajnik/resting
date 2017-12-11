package com.dgkrajnik.kotlinREST

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

// APIError functions bit from a Toptal tutorial.
data class ApiError (
        // TODO: Privatise the fields.
        val status: HttpStatus?,
        @JsonFormat(shape = JsonFormat.Shape.STRING) val timestamp: LocalDateTime?,
        val message: String?,
        @JsonProperty("debug_message") var debugMessage: String?,
        @JsonProperty("sub_errors") val subErrors: List<ApiSubErrors>?
) {
    constructor() : this(
            null,
            LocalDateTime.now(),
            null,
            null,
            null
    )
    constructor(status: HttpStatus) : this(
            status,
            LocalDateTime.now(),
            null,
            null,
            null
    )
    constructor(status: HttpStatus, ex: Throwable) : this(
            status,
            LocalDateTime.now(),
            "Unexpected Error",
            ex.localizedMessage,
            null
    )
    constructor(status: HttpStatus, message: String, ex: Throwable) : this(
            status,
            LocalDateTime.now(),
            message,
            ex.localizedMessage,
            null
    )
}

open class ApiSubErrors

data class ApiValidationError(val `object`: String, val field: String, val rejectedValue: Any, val message: String)