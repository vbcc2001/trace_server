package com.kwanhor.trace.server.api;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.TraceConfig;

@RestController
@RequestMapping("/server")
public class ServerAPI {
	private final TraceConfig traceConfig;
	public ServerAPI(TraceConfig traceConfig) {
		super();
		this.traceConfig = traceConfig;
	}
	@GetMapping("/ver")
	Properties getVersion() throws IOException {
		return PropertiesLoaderUtils.loadAllProperties("version");	
	}
	@PostMapping("/clientType")
	String getClientType() {
		return traceConfig.getClientType();
	}
}
