package com.kwanhor.trace.server.advice;

import java.util.NoSuchElementException;

import org.hibernate.PropertyValueException;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwanhor.trace.server.error.ErrorData;
import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.RespEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<Object>{

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return !RespEntity.class.isAssignableFrom(returnType.getNestedParameterType());
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		final RespEntity re=RespEntity.builder().code(0).data(body).build();
		if(body instanceof String) {
			try {
				return new ObjectMapper().writeValueAsString(re);
			} catch (JsonProcessingException e) {
				return re.toString();
			}
		}
		return re;
	}
	
	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(RestException.class)
	public RespEntity restExceptionHandler(RestException e) {
		return RespEntity.builder().code(-1)
				.data(e.getMessage())
				.build();
	}
	
	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public RespEntity dataExceptionHandler(DataIntegrityViolationException e) {
		Throwable cause=e.getCause();		
		if(cause instanceof PropertyValueException) {
			PropertyValueException pve=(PropertyValueException) cause;
			String entityName=pve.getEntityName();
			try {
				entityName=Class.forName(entityName).getSimpleName();
			} catch (ClassNotFoundException e1) {				
				e1.printStackTrace();
			}
			String fieldName=pve.getPropertyName();
			return RespEntity.builder().code(-1)
					.data(ErrorData.builder().code(400).msg(entityName+"属性"+fieldName+"不能为空").build())
					.build();
		}
		long now=System.currentTimeMillis();
		String msg="see log $"+now;
		log.error("$"+now+":"+e.getMostSpecificCause().getLocalizedMessage());
		return RespEntity.builder().code(-1)
		.data(ErrorData.builder().code(400).msg("数据违反完整性约束").detailMsg(msg).build())
		.build();
	}
	
	@ResponseBody
	@ResponseStatus(code = HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public RespEntity exceptionHandler(Exception e) {
		String msg;
		String detailMsg;
		if(e instanceof HttpMessageNotReadableException) {
			HttpMessageNotReadableException me=(HttpMessageNotReadableException) e;
			msg="请求参数格式错误";
			detailMsg=me.getLocalizedMessage();	
		}else if(e instanceof HttpMediaTypeNotSupportedException) {
			HttpMediaTypeNotSupportedException me=(HttpMediaTypeNotSupportedException) e;
			msg="非法请求类型:"+me.getContentType();
			detailMsg=me.getLocalizedMessage();
		}else if(e instanceof HttpRequestMethodNotSupportedException) {
			HttpRequestMethodNotSupportedException he=(HttpRequestMethodNotSupportedException) e;
			msg="非法请求方法:"+he.getMethod();
			detailMsg=msg;
		}else if(e instanceof NoSuchElementException) {
			msg="目标未入库";
			detailMsg=msg;
		}else {
			detailMsg="see log $"+System.currentTimeMillis();
			msg="未知错误";
		}
		log.error(detailMsg,e);
		return RespEntity.builder().code(-1)
				.data(ErrorData.builder().code(500).msg(msg).detailMsg(detailMsg).build())
				.build();
	}

}
