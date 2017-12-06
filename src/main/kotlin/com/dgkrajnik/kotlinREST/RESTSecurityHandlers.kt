package com.dgkrajnik.kotlinREST

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// Standard auth entry point.
@Component
class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence (
            request: HttpServletRequest,
            response: HttpServletResponse,
            authException: AuthenticationException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

// Handle auth requests. Handling is the same as the default success handler except
// that we don't redirect; we send a 200 OK instead.
class RestAuthSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
    private var requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess (
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication) {
        val savedRequest: SavedRequest? = requestCache.getRequest(request, response)
        if (savedRequest != null) {
            clearAuthenticationAttributes(request)
            return
        } else if (isAlwaysUseDefaultTargetUrl ||
                (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            requestCache.removeRequest(request, response)
            clearAuthenticationAttributes(request)
            return
        }

        clearAuthenticationAttributes(request)
    }

    fun setRequestCache(requestCache: RequestCache) {
        this.requestCache = requestCache;
    }
}


