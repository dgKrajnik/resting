package com.dgkrajnik.kotlinREST

import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class CustomSpringEventListener : ApplicationListener<AuditApplicationEvent> {
    val logger: Logger = LoggerFactory.getLogger("HelloLogger")

    override fun onApplicationEvent(event: AuditApplicationEvent) {
        logger.info("Received spring custom event - " + event.auditEvent.type + "\nFrom principal: ${event.auditEvent.principal}");
    }
}