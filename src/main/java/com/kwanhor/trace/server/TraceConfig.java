package com.kwanhor.trace.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "trace.config")
@Component
@Getter
@Setter
public class TraceConfig {
	private String clientType;//客户端类型
	
}
