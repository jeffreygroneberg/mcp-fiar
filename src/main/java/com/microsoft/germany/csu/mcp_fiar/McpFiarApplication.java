package com.microsoft.germany.csu.mcp_fiar;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.microsoft.germany.csu.mcp_fiar.service.api.FiarService;

@SpringBootApplication
public class McpFiarApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpFiarApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider fiarTools(FiarService fiarService) {
		return MethodToolCallbackProvider.builder().toolObjects(fiarService).build();
	}

}
