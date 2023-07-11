package com.kwanhor.trace.server.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.kwanhor.trace.server.model.Pallet;
public interface PalletRepo extends JpaRepository<Pallet, Long>{
	
	@Query("from Pallet as p where p.SNCode like ?1%")
	Page<Pallet> findBySNCode(String SNCode,Pageable pageable);
	Pallet findBySNCode(String SNCode);
	Pallet findByNickName(String nickName);
	@Query("select count(*) from Pallet as p where p.SNCode like ?1%")
	long getCountBySNCode(String SNCode);
	@Transactional(timeout=5)
	int deleteBySNCode(String SNCode);
}