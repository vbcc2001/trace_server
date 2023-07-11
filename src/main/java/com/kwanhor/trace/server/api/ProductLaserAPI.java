package com.kwanhor.trace.server.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.kwanhor.trace.server.model.ProductDetailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.CartoonBox;
import com.kwanhor.trace.server.model.ProductLaser;
import com.kwanhor.trace.server.model.Shipping;
import com.kwanhor.trace.server.query.PageData;
import com.kwanhor.trace.server.repo.CartoonBoxRepo;
import com.kwanhor.trace.server.repo.ProductLaserRepo;
import com.kwanhor.trace.server.util.ModelUtil;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/productLaser")
@Slf4j
//@Component
public class ProductLaserAPI {
	@Autowired
	private ProductLaserRepo productLaserRepo;
	@Autowired
	private CartoonBoxRepo cartoonBoxRepo;

    // 在持久层注入
    @PersistenceContext
    EntityManager entityManager;

	@PostMapping("/addAll")
	Iterable<ProductLaser> addAll(@RequestBody List<ProductLaser> productLaserList) {
		if(productLaserList==null||productLaserList.isEmpty())
			return Collections.emptyList();
		return productLaserRepo.saveAllAndFlush(productLaserList);
	}
	@PostMapping("/getByCode")
	PageData getBySnCode(String SNCode,Integer pageIndex,Integer pageSize){
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");	
		final Pageable pageable=PageRequest.of(pageIndex==null||pageIndex<0?0:pageIndex, pageSize==null||pageSize<0?30:pageSize, Sort.by("streamCode"));//默认升序排序
		Page<ProductLaser> page=productLaserRepo.findBySNCode(SNCode,pageable);
		return PageData.fromPage(page);
	}
	@PostMapping("/updateById")
	void updateById(@RequestBody ProductLaser productLaser) {
		if(productLaser==null)
			throw new RestException("未指定更新产品");
		final Long id=productLaser.getId();
		if(id==null)
			throw new RestException("更新失败，未指定产品ID");
		ProductLaser persist=productLaserRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,产品ID不存在:"+id);
		ModelUtil.merge(productLaser, persist);
		productLaserRepo.flush();
	}
	
	@PostMapping("/updateByCode") // {{server}}/productLaser/getByCode
	ProductLaser updateBySNCode(@RequestBody ProductLaser productLaser) {
		if(productLaser==null)
			throw new RestException("未指定更新产品");
		final String code=productLaser.getSNCode();
		if(code==null||code.isEmpty())
			throw new RestException("更新失败，未指定SN码");
		ProductLaser persist=productLaserRepo.findBySNCode(code);
		if(persist==null)
			throw new RestException("更新失败,无效SN码:"+code);
		ModelUtil.merge(productLaser, persist);
		productLaserRepo.flush();
		return persist;
	}
	
	@PostMapping("/getByBoxCode") // {{server}}/productLaser/getByBoxCode?cartoonBoxCode=
	List<ProductLaser> getByBoxCode(String cartoonBoxCode){//兄弟,记得传入nickName
		if(cartoonBoxCode==null)
			throw new RestException("未指定外箱编码cartoonBoxCode");
		return productLaserRepo.findByCartoonBoxCode(cartoonBoxCode, Sort.by("streamCode"));
//		return String.join(",", productLaserRepo.getLaserCodeByBoxCode(cartoonBoxCode, Sort.by("streamCode")));
	}

	@PostMapping("/getByPalletNickName") // {{server}}/productLaser/getByPalletCode?cartoonBoxCode=
	List<ProductLaser> getByPalletNickName(String palletNickName){//兄弟,记得传入nickName
		if(palletNickName==null)
			throw new RestException("未指定外箱编码palletCode");
		List<CartoonBox> boxs = cartoonBoxRepo.findByPalletCode(palletNickName, Sort.by("streamCode"));
        List<String> boxNickNames = boxs.stream().map( i -> i.getNickName()).distinct().collect(Collectors.toList());
		return productLaserRepo.findByCartoonBoxCodeIn(boxNickNames, Sort.by("streamCode"));
	}

	
	@PostMapping("/clearBoxCode") // {{server}}/productLaser/clearBoxCode?boxCode=
	int clearBoxCode(String boxCode) {
		if(boxCode==null)
			throw new RestException("缺少参数boxCode");
		return productLaserRepo.removeLaserFromBox(boxCode);
	}

	@PostMapping("/clearShipCode") // {{server}}/productLaser/clearBoxCode?boxCode=
	int clearShipCode(String shipCode) {
		if(shipCode==null)
			throw new RestException("缺少参数shipCode");
		return productLaserRepo.removeLaserFromShip(shipCode);
	}

	@PostMapping("/clearShipCodeByBoxNickName")
	int clearShipCodeByBoxNickName(String boxNickName) {
		if(boxNickName==null)
			throw new RestException("缺少参数boxNickName");
		return productLaserRepo.removeLaserFromShipByBoxNickName(boxNickName);
	}

	@PostMapping("/clearShipCodeByPalletNickName")
	int clearShipCodeByPalletNickName(String palletNickName) {
		if(palletNickName==null)
			throw new RestException("缺少参数palletNickName");
        List<CartoonBox>  boxs =  cartoonBoxRepo.findByPalletCode(palletNickName, Sort.by("streamCode"));
        List<String> boxNickNames = boxs.stream().map( i -> i.getNickName()).distinct().collect(Collectors.toList());
		return productLaserRepo.removeLaserFromShipByBoxNickNames(boxNickNames);
	}

	
	@GetMapping("/count")
	Long getCount(String laserCode) {
		if(laserCode==null||laserCode.isEmpty())
			return productLaserRepo.count();
		return productLaserRepo.getCountBySNCode(laserCode);
	}
	@GetMapping("/shippingCount") // {{server}}/productLaser/shippingCount
	Long getShippingCount() {
		return productLaserRepo.getShippingCount();
	}
	/**
	 * 单个产品的出货信息,初版业务接口，目前已统一改为中箱/栈板出货
	 * @param SNCode 镭雕SN码
	 * @return 产品出货信息
	 */
	@PostMapping("/getShippingByLaserCode")// {{server}}/productLaser/getShippingByLaserCode?SNCode=
	Shipping getShippingBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("未指定产品编码SNCode");
		ProductLaser laser=productLaserRepo.findBySNCode(SNCode);
		Shipping shipping=Shipping.fromProductLaser(laser);//不含栈板SN的出货信息
		//求栈板信息
		CartoonBox box=cartoonBoxRepo.findBySNCode(shipping.getCartoonBoxSN());
		shipping.setPalletSN(box.getPalletCode());
		return shipping;
	}
	/**
	 * 
	 * @param SNCode 产品唯一SN码
	 * @return 若SN码存在则返回对应的产品信息，否则返回空
	 */
	@GetMapping("/getBySN")// {{server}}/productLaser/getBySN?SNCode=
	ProductLaser getBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("未指定产品编码SNCode");
		ProductLaser laser=productLaserRepo.findBySNCode(SNCode);
		return laser;
	}
	
	/**
	 * 查询出货结果
	 * @param pageIndex
	 * @param pageSize
	 * @param isBox
	 * @return
	 */
	@PostMapping("/getShippings") // {{server}}/productLaser/getShippings?pageIndex=&pageSize=
	PageData getShippings(Integer pageIndex,Integer pageSize,boolean isBox){
		final Pageable pagable;
		if(pageIndex==null||pageIndex<0) {//查询所有产品的出货信息
			pagable=Pageable.unpaged();
		}else {
			if(pageSize==null)
				throw new RestException("缺少参数pageSize");
			if(pageSize<0)
				throw new RestException("分页大小不能为负数:"+pageSize);
			pagable=PageRequest.of(pageIndex, pageSize);//分页查询
		}
		Page<ProductLaser> page=productLaserRepo.getShippings(pagable);
		PageData pageData=PageData.fromPage(page);
		List<ProductLaser> dataList=page.getContent();
		List<Shipping> shippings=new LinkedList<>();//出货产品信息
		Set<String> boxCodes=new HashSet<>();//中箱nickName
		for(ProductLaser laser:dataList) {//产品转出货信息
			Shipping shipping=Shipping.fromProductLaser(laser);//不含栈板SN的出货信息
			shippings.add(shipping);
			boxCodes.add(laser.getCartoonBoxCode());
		}
		//查询栈板SN
		List<Map<String, String>> palletCodeMap=cartoonBoxRepo.getPalletCodes(boxCodes);
		if(palletCodeMap==null||palletCodeMap.isEmpty()) {//可能是中箱出货
//			throw new RestException("出货产品没有找到栈板信息");
		}else {
			Map<String, String> pns=new HashMap<>();//boxCode->palletCode 中箱nickName->栈板nickName
			for(Map<String, String> map:palletCodeMap) {
				pns.put(map.get("boxCode"), map.get("palletCode"));
			}
			for(Shipping s:shippings) {
				final String pCode=pns.get(s.getCartoonBoxSN());
				if(pCode==null||pCode.isEmpty())continue;
//					throw new RestException("出货产品的外箱"+s.getCartoonBoxSN()+"没有对应的栈板信息");
				s.setPalletSN(pCode);
			}
		}
		pageData.setContent(shippings);
		return pageData;
	}
	
	@PostMapping("/delById") // {{server}}/productLaser/delById
	void delById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要删除的数据ID");	
		try {
			productLaserRepo.deleteAllById(ids);
		}catch (IllegalArgumentException e) {
			throw new RestException("删除失败,参数错误:"+ids);	
		}
	}
	@PostMapping("/get") // {{server}}/productLaser/get
	Collection<ProductLaser> getProductLaser(@RequestBody ProductLaser probe){
		if(probe==null)
			throw new RestException("未指定查询条件");
		try {
			Example<ProductLaser> example=Example.of(probe);
			return productLaserRepo.findAll(example);
		}catch (Throwable t) {
			log.error("查询失败",t);
			throw new RestException("查询失败");
		}	
	}

	@GetMapping("/getByExtendSql")// {{server}}/productLaser/getByExtendSql?extendSql=
	List<ProductDetailInfo> getByExtendSql(String extendSql,int pageSize, int pageIndex) {
		if(extendSql==null||extendSql.isEmpty())
			throw new RestException("extendSql 不能为空");
        int offset = pageSize * (pageIndex-1);
        String base_sql = """ 
        			select laser.*,
        			    box.create_date as box_date ,
  						pallet.nick_name as pallet_code,pallet.create_date as pallet_date
  					from product_laser as laser join cartoon_box as box join pallet 
  					on box.pallet_code = pallet.nick_name on laser.cartoon_box_code = box.nick_name 
  					""";
		String sql = base_sql + " where "+extendSql+" limit "+pageSize+" offset "+offset;
        @SuppressWarnings("unchecked")
		List<ProductDetailInfo> resultList = entityManager.createNativeQuery(sql, ProductDetailInfo.class).getResultList();
		return resultList;
	}

	@GetMapping("/getExtendSqlCount")// {{server}}/productLaser/getByExtendSql?extendSql=
	Object getExtendSqlCount(String extendSql) {
		if(extendSql==null||extendSql.isEmpty())
			throw new RestException("extendSql 不能为空");
        String base_sql = "select count(*) as count from product_laser as laser join cartoon_box as box join pallet on box.pallet_code = pallet.nick_name on laser.cartoon_box_code = box.nick_name";
		String sql = base_sql + " where "+extendSql;
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        System.out.println(result);
		return result;
	}
	@PostMapping("/bindBox")// {{server}}/productLaser/bindBox?boxCode=xx  [laserCode1,...]
	int bindBox(String boxCode,@RequestBody List<String> laserCodes) {//批量绑定中箱
		if(boxCode==null)
			throw new RestException("缺少参数boxCode");
		if(laserCodes==null||laserCodes.isEmpty())
			throw new RestException("body未指定产品SN码");
		return productLaserRepo.bindBox(boxCode, laserCodes);//产品SN码可能未入库/产品的中箱编码可能被覆盖/中箱编码可能未入库
	}
	@PostMapping("/setUsed")//{{server}}/productLaser/setUsed?used=xx  [laserCode1,...]
	int setUsed(String used,@RequestBody List<String> laserCodes) {//批量修改彩盒打印标记
		return productLaserRepo.setUsed(used, laserCodes);
	}
	@PostMapping("/setWeight")//{{server}}/productLaser/setWeight?weight=xx&id=xx
	ProductLaser setWeight(String weight,Long id) {
		ProductLaser laser=productLaserRepo.findById(id).orElseThrow();
		laser.setWeight(weight);
		return productLaserRepo.save(laser);
	}
}
