package com.kwanhor.trace.server.repo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.kwanhor.trace.server.model.CartoonBox;
public interface CartoonBoxRepo extends JpaRepository<CartoonBox, Long>{
	
	@Query("from CartoonBox as p where p.SNCode like ?1%")
	Page<CartoonBox> findBySNCode(String SNCode,Pageable pageable);
	CartoonBox findBySNCode(String SNCode);
	CartoonBox findByNickName(String nickName);
	@Query("select count(*) from CartoonBox as p where p.SNCode like ?1%")
	long getCountBySNCode(String SNCode);
	@Transactional(timeout=5)
	int deleteBySNCode(String SNCode);
	/**
	 * 
	 * @param boxCodes 中箱nickName
	 * @return 中箱关联的栈板信息(中箱nickName和栈板nickName)
	 */
	@Query("select nickName as boxCode,palletCode as palletCode from CartoonBox where nickName in (?1)")
	List<Map<String, String>> getPalletCodes(Collection<String> boxCodes);
	/**
	 * 
	 * @param palletCode 栈板nickName
	 * @param sort 排序条件
	 * @return 栈板包含的所有中箱SN码
	 */
	@Query("select c.SNCode from CartoonBox as c where c.palletCode=?1")
	List<String> getSNCodeByPalletCode(String palletCode,Sort sort);
	List<CartoonBox> findByPalletCode(String palletCode,Sort sort);
	/**
	 * 
	 * 从指定栈板中移除外箱
	 * @param palletCode 栈板编码
	 * @return 被移除的外箱数量
	 */
	@Modifying
	@Query("update CartoonBox set palletCode='' where palletCode=?1")
	@Transactional(timeout=10)
	int removeBoxFromPallet(String palletCode);
	@Modifying
	@Query("update CartoonBox set palletCode=:palletNickName where nickName in(:nickNames) and (palletCode is null or palletCode='')") //前端传入的是nickName
	@Transactional(timeout=10)
	int bindPallet(String palletNickName,List<String> nickNames);	
}
