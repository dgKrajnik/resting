package com.dgkrajnik.kotlinREST

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import javax.inject.Inject

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    @Inject
    private lateinit var restAuthenticationEntryPoint: RestAuthenticationEntryPoint

    @Inject
    private lateinit var authenticationSuccessHandler: RestAuthSuccessHandler

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser("gary").password("temppass").roles("ADMIN")
                .and()
                .withUser("steve").password("userpass").roles("USER");
    }

    override fun configure(http: HttpSecurity) {
        http
            .exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .authorizeRequests()
                .antMatchers("/hello/secureData").authenticated()
            .and()
                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(SimpleUrlAuthenticationFailureHandler())
            .and()
                .logout();
    }
}