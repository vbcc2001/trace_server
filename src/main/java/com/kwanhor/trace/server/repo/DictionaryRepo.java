package com.kwanhor.trace.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.kwanhor.trace.server.model.Dictionary;

public interface DictionaryRepo extends JpaRepository<Dictionary, Long>{
//	List<Dictionary> findAllByIsDeleted(boolean isDel);
	/**
	 * 逻辑删除字典项
	 * @param dicType 字典类型
	 * @return 更新行数
	 */
	@Modifying
	@Transactional(timeout = 5)
	int deleteByDicType(String dicType);
	
	Iterable<Dictionary> getByDicType(String dicType);
	Dictionary findByDicTypeAndCode(String dicType,String code);
}	
