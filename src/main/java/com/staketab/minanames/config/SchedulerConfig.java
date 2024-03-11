package com.staketab.minanames.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduled.enabled", matchIfMissing = true)
public class SchedulerConfig {
}
