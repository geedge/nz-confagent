package com.nis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
public class NzConfagentApplication {

	public static void main(String[] args) {

		SpringApplication.run(NzConfagentApplication.class, args);
	}
	
	@Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("Confagent") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
	 
}
