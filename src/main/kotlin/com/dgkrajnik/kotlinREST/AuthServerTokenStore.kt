package com.dgkrajnik.kotlinREST

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class AuthServerTokenStore {
    @Value("classpath:schema.sql")
    lateinit var schemaScript: Resource

    fun dataSourceInitializer(dataSource: DataSource): DataSourceInitializer {
        val initializer = DataSourceInitializer()
        initializer.setDataSource(dataSource)
        initializer.setDatabasePopulator(databasePopulator())
        return initializer
    }

    private fun databasePopulator(): DatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)
        return populator
    }

    @Bean
    fun dataSource(): DataSource {
        val dataSource = EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("schema.sql").build()
        return dataSource as DataSource
    }
}
