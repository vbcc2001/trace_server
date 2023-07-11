package com.kwanhor.trace.server.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.CartoonBox;
import com.kwanhor.trace.server.model.ProductLaser;
import com.kwanhor.trace.server.query.PageData;
import com.kwanhor.trace.server.repo.CartoonBoxRepo;
import com.kwanhor.trace.server.repo.ProductLaserRepo;
import com.kwanhor.trace.server.util.ModelUtil;

import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cartoonBox")
@Slf4j
public class CartoonBoxAPI {	
	private final CartoonBoxRepo cartoonBoxRepo;
	private final ProductLaserRepo productLaserRepo;
	
	public CartoonBoxAPI(CartoonBoxRepo cartoonBoxRepo, ProductLaserRepo productLaserRepo) {
		super();
		this.cartoonBoxRepo = cartoonBoxRepo;
		this.productLaserRepo = productLaserRepo;
	}

	@PostMapping("/addAll") // {{server}}/cartoonBox/addAll
	Iterable<CartoonBox> addAll(@RequestBody List<CartoonBox> cartoonBoxList) {
		if(cartoonBoxList==null||cartoonBoxList.isEmpty())
			return Collections.emptyList();
		return cartoonBoxRepo.saveAllAndFlush(cartoonBoxList);
	}
	
	@PostMapping("/getCodeLike") // {{server}}/cartoonBox/getCodeLike?SNCode=&pageIndex=&pageSize=
	PageData getBySNCode(String SNCode,Integer pageIndex,Integer pageSize){
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");	
		final Pageable pageable=PageRequest.of(pageIndex==null||pageIndex<0?0:pageIndex, pageSize==null||pageSize<0?30:pageSize, Sort.by("streamCode"));//默认升序排序
		Page<CartoonBox> page=cartoonBoxRepo.findBySNCode(SNCode,pageable);
		return PageData.fromPage(page);
	}
	
	@PostMapping("/getByCode") // {{server}}/cartoonBox/getByCode?SNCode=
	CartoonBox getBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");
		return cartoonBoxRepo.findBySNCode(SNCode);
	}
		
	@PostMapping("/delByCode") // {{server}}/cartoonBox/delByCode?SNCode=
	int delBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");	
		return cartoonBoxRepo.deleteBySNCode(SNCode);
	}
	@PostMapping("/delById") // {{server}}/cartoonBox/delById
	void delById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要删除的数据ID");	
		try {
			cartoonBoxRepo.deleteAllById(ids);
		}catch (IllegalArgumentException e) {
			throw new RestException("删除失败,参数错误:"+ids);	
		}
	}
	
	@PostMapping("/updateById") // {{server}}/cartoonBox/updateById
	void updateById(@RequestBody CartoonBox cartoonBox) {
		if(cartoonBox==null)
			throw new RestException("未指定更新产品");
		final Long id=cartoonBox.getId();
		if(id==null)
			throw new RestException("更新失败，未指定产品ID");
		CartoonBox persist=cartoonBoxRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,产品ID不存在:"+id);
		ModelUtil.merge(cartoonBox, persist);
		cartoonBoxRepo.flush();
	}
	
	@PostMapping("/updateByCode") // {{server}}/cartoonBox/updateByCode
	CartoonBox updateBySNCode(@RequestBody CartoonBox cartoonBox) {
		if(cartoonBox==null)
			throw new RestException("未指定更新产品");
		final String code=cartoonBox.getSNCode();
		if(code==null||code.isEmpty())
			throw new RestException("更新失败，未指定SN码");
		CartoonBox persist=cartoonBoxRepo.findBySNCode(code);
		if(persist==null)
			throw new RestException("更新失败,无效SN码:"+code);
		ModelUtil.merge(cartoonBox, persist);
		cartoonBoxRepo.flush();
		return persist;
	}
	
	@GetMapping("/count") // {{server}}/cartoonBox/count?SNCode=
	Long getCount(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			return cartoonBoxRepo.count();
		return cartoonBoxRepo.getCountBySNCode(SNCode);
	}
	@PostMapping("/getSNByPalletCode") // {{server}}/cartoonBox/getSNByPalletCode?palletCode=
	List<CartoonBox> getCartoonBoxByPallet(String palletCode){
		if(palletCode==null||palletCode.isEmpty())
			throw new RestException("未指定栈板编码");
//		List<String> SNCodes=cartoonBoxRepo.getSNCodeByPalletCode(palletCode, Sort.by("streamCode"));
//		return String.join(",", SNCodes);
		return cartoonBoxRepo.findByPalletCode(palletCode, Sort.by("streamCode"));
	}
	
	@PostMapping("/clearPalletCode") // {{server}}/cartoonBox/clearPalletCode?palletCode=
	int clearPalletCode(String palletCode) {
		if(palletCode==null)
			throw new RestException("未指定栈板编码palletCode");
		return cartoonBoxRepo.removeBoxFromPallet(palletCode);
	}
	@PostMapping("/get") // {{server}}/cartoonBox/get
	Collection<CartoonBox> getCartoonBox(@RequestBody CartoonBox probe){
		if(probe==null)
			throw new RestException("未指定查询条件");
		try {
			Example<CartoonBox> example=Example.of(probe);
			return cartoonBoxRepo.findAll(example);
		}catch (Throwable t) {
			log.error("查询失败",t);
			throw new RestException("查询失败");
		}	
	}
	@PostMapping("/byNickName")
	CartoonBox byNickName(String nickName) {// {{server}}/cartoonBox/byNickName?nickName=xx
		if(nickName==null||nickName.isBlank())return null;
		return cartoonBoxRepo.findByNickName(nickName);
	}
	
	@PostMapping("/inputLaser") // {{server}}/cartoonBox/inputLaser {box}
	@Transactional
	public boolean inputLaser(@RequestBody CartoonBox box) {//栈板装箱,栈板入库并绑定中箱nickName
		//血淋淋的教训,开启事务的方法必需用public来修饰，确保方法访问权限
		String nickName=box.getNickName();
		if(nickName==null||nickName.isBlank())
			throw new RestException("中箱未指定SN码(NickName)");
		List<String> laserSNCodes=box.getLaserSNCodes();
		if(laserSNCodes==null||laserSNCodes.isEmpty()) 
			throw new RestException("未指定装箱产品");		
		CartoonBox rs=cartoonBoxRepo.save(box);
		int bindCount=productLaserRepo.bindBox(rs.getNickName(), laserSNCodes);//绑定中箱数
		if(bindCount!=laserSNCodes.size())
			throw new RestException("可能存在未入库或者重复装箱的产品");		
		return true;
	}

	@PostMapping("/bindShip")// {{server}}/cartoonBox/bindShip?shipCode=xx  [boxCode1,...]
	int bindShip(String shippingCode, String shippingDate, @RequestBody CartoonBox box) {//出货单号批量绑定中箱
		if(shippingCode==null)
			throw new RestException("缺少参数shippingCode");
		if(shippingDate==null)
			throw new RestException("缺少参数shippingDate");
		if(box==null)
			throw new RestException("body未指定box");
        String nickName=box.getNickName();
        List<ProductLaser> lasers = productLaserRepo.findByCartoonBoxCode(nickName, Sort.by("streamCode"));
        List<String> list = lasers.stream().map( i -> i.getSNCode()).distinct().collect(Collectors.toList());
		return productLaserRepo.bindShip(shippingCode, shippingDate, list);//产品SN码可能未入库/产品的中箱编码可能被覆盖/中箱编码可能未入库
	}

}
