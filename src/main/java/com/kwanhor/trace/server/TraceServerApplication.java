package com.kwanhor.trace.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class TraceServerApplication {
	public static void main(String[] args) {
		initProperties();
		SpringApplication.run(TraceServerApplication.class, args);
		
	}
	
	public static void stop(String[] args) {
//		SpringApplication.exit(context);
		System.exit(0);
	}
	
	private static void initProperties() {
		System.setProperty("spring.application.name", "trace-server");
	}
}
