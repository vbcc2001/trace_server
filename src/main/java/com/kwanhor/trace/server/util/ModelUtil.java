package com.kwanhor.trace.server.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.beans.BeanUtils;
import com.kwanhor.trace.server.error.RestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ModelUtil {
	/**
	 * 将源对象的值复制到目标对象
	 * @param <E> 模型类泛型
	 * @param source 源对象
	 * @param target 目标对象
	 */
	public static <E> void merge(E source,E target){
		if(source==null||target==null)
			return;
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod != null) {
				Method readMethod = targetPd.getReadMethod();
				if (readMethod != null) {
					try {
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if(value==null)
							continue;
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, value);
					}catch (Throwable t) {
						log.error("更新失败",t);
						throw new RestException("更新失败,字段"+targetPd.getName()+"读写错误");	
					}
				}
			}
		}
	}
}
