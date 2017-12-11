package com.dgkrajnik.kotlinREST

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.inject.Inject
import javax.inject.Named
import javax.sql.DataSource

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration : AuthorizationServerConfigurerAdapter() {
    @Inject
    lateinit var dataSource: DataSource

    @Inject
    lateinit var tokenStore: TokenStore

    @Inject
    @Named("authenticationManagerBean")
    lateinit var authenticationManager: AuthenticationManager

    @Inject
    lateinit var userApprovalHandler: TokenStoreUserApprovalHandler

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient("normalClient")
                .authorizedGrantTypes("password", "implicit")
                .scopes("read")
                .secret("spookysecret")
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager)
    }

    override fun configure(oAuthServer: AuthorizationServerSecurityConfigurer) {
        oAuthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }
}
