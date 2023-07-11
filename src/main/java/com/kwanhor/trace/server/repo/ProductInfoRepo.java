package com.kwanhor.trace.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.kwanhor.trace.server.model.ProductInfo;

public interface ProductInfoRepo extends JpaRepository<ProductInfo, Long>{
	@Transactional(timeout = 5)
	int deleteByProductPartNumber(String productPartNumber);
}
