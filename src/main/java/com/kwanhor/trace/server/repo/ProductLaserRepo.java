package com.kwanhor.trace.server.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.kwanhor.trace.server.model.ProductLaser;

public interface ProductLaserRepo extends JpaRepository<ProductLaser, Long>{
	
	@Query("from ProductLaser as p where p.SNCode like ?1%")
	Page<ProductLaser> findBySNCode(String SNCode,Pageable pageable);
	@Query("select count(*) from ProductLaser as p where p.SNCode like ?1%")
	long getCountBySNCode(String SNCode);
	/**
	 * 
	 * @param shippngCode 出货码
	 * @return 已标记出货码的产品总数，即出货数量
	 */
	@Query("select count(*) from ProductLaser as p where p.shippingCode is not null and p.shippingCode<>''")
	long getShippingCount();
	ProductLaser findBySNCode(String SNCode);
	List<ProductLaser> findByCartoonBoxCode(String cartoonBoxCode,Sort sort);
	List<ProductLaser> findByCartoonBoxCodeIn(List<String> cartoonBoxCodes,Sort sort);
	@Query("select p.SNCode from ProductLaser as p where cartoonBoxCode=?1")
	List<String> getLaserCodeByBoxCode(String cartoonBoxCode,Sort sort);
	/**
	 * 
	 * 从指定外箱中移出所有产品
	 * @param boxCode 外箱编码
	 * @return 被移除的产品总数
	 */
	@Modifying
	@Query("update ProductLaser p set p.cartoonBoxCode='' where p.cartoonBoxCode=?1")
	@Transactional(timeout=10)
	int removeLaserFromBox(String cartoonBoxCode);

	@Modifying
	@Query("update ProductLaser p set p.shippingCode='' where p.shippingCode=?1")
	@Transactional(timeout=10)
	int removeLaserFromShip(String shippingCode);

    /**
     * 解除单个中箱的订单号
     */
	@Modifying
	@Query("update ProductLaser p set p.shippingCode='' where p.cartoonBoxCode=?1")
	@Transactional(timeout=10)
	int removeLaserFromShipByBoxNickName(String boxNickName);

    /**
     * 解除多个中箱的订单号
     */
	@Modifying
	@Query("update ProductLaser p set p.shippingCode='' where p.cartoonBoxCode in(:boxNickNames)")
	@Transactional(timeout=10)
	int removeLaserFromShipByBoxNickNames(List<String> boxNickNames);


	
	@Query("from ProductLaser as p where p.shippingCode<>''")
	Page<ProductLaser> getShippings(Pageable pageable);
	@Modifying
	@Query("update ProductLaser p set p.cartoonBoxCode=:boxCode where p.SNCode in(:laserCodes)")
	@Transactional(timeout=10)
	int bindBox(String boxCode,Collection<String> laserCodes);
	@Modifying
	@Query("update ProductLaser p set p.shippingCode=:shippingCode,p.shippingDate=:shippingDate where p.SNCode in(:laserCodes)")
	@Transactional(timeout=10)
	int bindShip(String shippingCode, String shippingDate, Collection<String> laserCodes);

	@Modifying
	@Query("update ProductLaser p set p.used=:used where p.SNCode in(:laserCodes)")
	@Transactional(timeout=10)
	int setUsed(String used,Collection<String> laserCodes);//标记为是否已打印彩盒标签
	@Modifying
	@Query("update ProductLaser set cartoonBoxCode=:boxNickName where SNCode in(:SNCodes) and (cartoonBoxCode is null or cartoonBoxCode='')")
	@Transactional(timeout=10)
	int bindBox(String boxNickName,List<String> SNCodes);	
 }
