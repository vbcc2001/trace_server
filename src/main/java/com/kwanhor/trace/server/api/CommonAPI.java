package com.kwanhor.trace.server.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class CommonAPI {
	private final EntityManager entityManager;

	public CommonAPI(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}
	@PostMapping("/query")
	List<?> query(@RequestBody Map<String, String> body){
		String sql=body.get("sql");
		if(sql==null)return Collections.emptyList();
		return entityManager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();		
	}
	@PostMapping("/get")
	Object getOne(@RequestBody Map<String, String> body){
		String sql=body.get("sql");
		if(sql==null)return Collections.emptyList();
		return entityManager.createNativeQuery(sql).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();		
	}
}
