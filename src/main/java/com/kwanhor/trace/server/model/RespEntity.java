package com.kwanhor.trace.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespEntity {
	int code;//0表示成功,-1表示失败
	@JsonInclude(value = Include.NON_NULL)
	Object data;//操作数据
}
