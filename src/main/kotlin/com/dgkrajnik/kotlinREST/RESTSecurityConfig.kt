package com.dgkrajnik.kotlinREST

import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler
import org.springframework.security.oauth2.provider.token.RemoteTokenServices
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.inject.Inject

@Configuration
@EnableResourceServer
class WebSecurityConfig : ResourceServerConfigurerAdapter() {
    @Inject
    lateinit private var authenticationManagerBean: AuthenticationManager

    @Primary
    @Bean
    fun tokenService(): RemoteTokenServices {
        val tokenService = RemoteTokenServices()
        // Note that, because the server port is set *after*
        // the beans get constructed, you can't just use local.server.port.
        tokenService.setCheckTokenEndpointUrl(
                "http://localhost:8080/oauth/check_token")
        tokenService.setClientId("normalClient")
        tokenService.setClientSecret("spookysecret")
        return tokenService
    }

    override fun configure(http: HttpSecurity) {
        //@formatter:off
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
            .and()
                .authorizeRequests()
                    .antMatchers("/hello/secureOAuthData").access("#oauth2.hasScope('read')")
                    .antMatchers("/hello/string").permitAll()
                    .antMatchers("/hello/service").permitAll()
            .and()
                .authorizeRequests()
                    .antMatchers("/hello/secureData").authenticated()
        //@formatter:on


        http.addFilterBefore(BasicAuthenticationFilter(authenticationManagerBean),
                UsernamePasswordAuthenticationFilter::class.java)
    }
}

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {
    //@Inject
    lateinit private var securityConfig: AuthorizationServerSecurityConfiguration

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return OAuth2MethodSecurityExpressionHandler()
    }
}
