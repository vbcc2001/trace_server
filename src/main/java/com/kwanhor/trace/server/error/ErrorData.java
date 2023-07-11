package com.kwanhor.trace.server.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorData {
	private int code;//错误码
	private String msg;//错误提示
	private String detailMsg;//详细提示
	
}
