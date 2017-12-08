package com.dgkrajnik.kotlinREST

import com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler
import org.springframework.security.oauth2.provider.token.RemoteTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import javax.inject.Inject
import javax.sql.DataSource

@Configuration
@EnableResourceServer
class WebSecurityConfig: ResourceServerConfigurerAdapter() {
    @Inject
    lateinit var dataSource: DataSource

    fun dataSource(): DataSource {
        val dataSource = EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .build()
        return dataSource as DataSource
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
            .and()
                .authorizeRequests()
                    .antMatchers("/hello/secureOAuthData").access("#oauth2.hasScope('read')")
                    .antMatchers("/hello/string").permitAll()
                    .antMatchers("/hello/service").permitAll()
            .and()
    }
}

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig: GlobalMethodSecurityConfiguration() {
    //@Inject
    lateinit private var securityConfig: AuthorizationServerSecurityConfiguration

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return OAuth2MethodSecurityExpressionHandler()
    }
}

@Configuration
@Order(100)
@EnableWebSecurity
class BasicResourceSecurity: WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint("Bearer realm=\"webrealm\""))
                .and()
                .authorizeRequests()
                .antMatchers("/hello/secureData").authenticated()
                .and()
                .httpBasic()
    }

    @Inject
    fun globalUserDetails(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser("steve").password("userpass").roles("USER")
    }
}
