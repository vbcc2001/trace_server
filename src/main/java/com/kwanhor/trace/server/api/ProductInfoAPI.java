package com.kwanhor.trace.server.api;

import java.util.Collections;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.DataRecycle;
import com.kwanhor.trace.server.model.ProductInfo;
import com.kwanhor.trace.server.query.PageData;
import com.kwanhor.trace.server.query.Query;
import com.kwanhor.trace.server.repo.DataRecycleRepo;
import com.kwanhor.trace.server.repo.ProductInfoRepo;
import com.kwanhor.trace.server.util.ModelUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/productinfo")
@Slf4j
public class ProductInfoAPI {	
	private final ProductInfoRepo productInfoRepo;
	private final DataRecycleRepo dataRecycleRepo;	
	public ProductInfoAPI(ProductInfoRepo productInfoRepo, DataRecycleRepo dataRecycleRepo) {
		super();
		this.productInfoRepo = productInfoRepo;
		this.dataRecycleRepo = dataRecycleRepo;
	}
	@PostMapping("/addAll")
	Iterable<ProductInfo> addAll(@RequestBody List<ProductInfo> productInfoList) {
		if(productInfoList==null||productInfoList.isEmpty())
			return Collections.emptyList();
		return productInfoRepo.saveAllAndFlush(productInfoList);
	}
	@PostMapping("/getAll")
	Iterable<ProductInfo> getAll(){
		return productInfoRepo.findAll();
	}
	@PostMapping("/update")
	ProductInfo updateOne(@RequestBody ProductInfo productInfo) {
		if(productInfo==null)
			return null;
		final Long id=productInfo.getId();
		if(id==null)
			throw new RestException("更新失败，未指定产品ID");
		ProductInfo persist=productInfoRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,产品ID不存在:"+id);
		ModelUtil.merge(productInfo, persist);
		productInfoRepo.flush();
		return persist;
	}
	@PostMapping("/delByCode")
	int delByCode(String productPartNumber){
		return productInfoRepo.deleteByProductPartNumber(productPartNumber);
	}
	@PostMapping("/delById") // {{server}}/productinfo/delById
	@Deprecated
	void delById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要删除的数据ID");	
		try {
			productInfoRepo.deleteAllById(ids);
		}catch (IllegalArgumentException e) {
			throw new RestException("删除失败,参数错误:"+ids);	
		}
	}		
	@GetMapping("/count")
	long getCount() {
		return productInfoRepo.count();
	}
	@PostMapping("/getAllByPage")
	PageData findAllbyPage(@RequestBody Query<ProductInfo> query) {
		return 	PageData.fromPage(productInfoRepo.findAll(query.toPageable()));
	}
	@PostMapping("/delOne")// {{server}}/productinfo/delOne?id=xx
	@Transactional
	public boolean delOne(Long id) {
		ProductInfo info=productInfoRepo.getReferenceById(id);
		if(info==null)return true;
		DataRecycle dr=new DataRecycle();
		dr.setFqcn(ProductInfo.class.getName());
		dr.setKeyName("id");
		dr.setKeyValue(id+"");
		try {
			dr.setData(new ObjectMapper().writeValueAsString(info));
		} catch (JsonProcessingException e) {
			log.error("序列化异常",e);
			throw new RestException("序列化异常");
		}
		dataRecycleRepo.save(dr);
		productInfoRepo.delete(info);
//		System.out.println(1/0);
		return true;
	}
}
