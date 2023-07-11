package com.kwanhor.trace.server.repo;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.kwanhor.trace.server.model.ProductCode;

public interface ProductCodeRepo extends JpaRepository<ProductCode, Long>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Transactional
	@Query("select p from ProductCode as p where p.prefix=:prefix and p.fqcn=:fqcn and p.isDay=:isDay")
	ProductCode selectRecord(String prefix,String fqcn,boolean isDay);
	@Modifying
	@Query("update ProductCode set pos=:pos where id=:id and pos=:oldPos")
	@Transactional
	int updatePos(long id,int pos,int oldPos);
}
