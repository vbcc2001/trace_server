package com.kwanhor.trace.server.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kwanhor.trace.server.model.Pallet;
import com.kwanhor.trace.server.model.ProductLaser;
import com.kwanhor.trace.server.query.PageData;
import com.kwanhor.trace.server.repo.CartoonBoxRepo;
import com.kwanhor.trace.server.repo.PalletRepo;
import com.kwanhor.trace.server.repo.ProductLaserRepo;
import com.kwanhor.trace.server.util.ModelUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pallet")
@Slf4j
public class PalletAPI {
	@Autowired
	private PalletRepo palletRepo;
	@Autowired
	private CartoonBoxRepo cartoonBoxRepo;
	
	@PostMapping("/addAll") // {{server}}/pallet/addAll
	Iterable<Pallet> addAll(@RequestBody List<Pallet> palletList) {
		if(palletList==null||palletList.isEmpty())
			return Collections.emptyList();
		return palletRepo.saveAllAndFlush(palletList);
	}
	
	@PostMapping("/getCodeLike") // {{server}}/pallet/getCodeLike?SNCode=&pageIndex=&pageSize=
	PageData getBySNCode(String SNCode,Integer pageIndex,Integer pageSize){
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");	
		final Pageable pageable=PageRequest.of(pageIndex==null||pageIndex<0?0:pageIndex, pageSize==null||pageSize<0?30:pageSize, Sort.by("streamCode"));//默认升序排序
		Page<Pallet> page=palletRepo.findBySNCode(SNCode,pageable);
		return PageData.fromPage(page);
	}
	
	@PostMapping("/getByCode") // {{server}}/pallet/getByCode?SNCode=
	Pallet getBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");
		return palletRepo.findBySNCode(SNCode);
	}
		
	@PostMapping("/delByCode") // {{server}}/pallet/delByCode?SNCode=
	int delBySNCode(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("缺少参数SNCode");	
		return palletRepo.deleteBySNCode(SNCode);
	}
	@PostMapping("/delById") // {{server}}/pallet/delById
	void delById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要删除的数据ID");	
		try {
			palletRepo.deleteAllById(ids);
		}catch (IllegalArgumentException e) {
			throw new RestException("删除失败,参数错误:"+ids);	
		}
	}	
	@PostMapping("/updateById") // {{server}}/pallet/updateById
	void updateById(@RequestBody Pallet pallet) {
		if(pallet==null)
			throw new RestException("未指定更新产品");
		final Long id=pallet.getId();
		if(id==null)
			throw new RestException("更新失败，未指定产品ID");
		Pallet persist=palletRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,产品ID不存在:"+id);
		ModelUtil.merge(pallet, persist);
		palletRepo.flush();
	}
	
	@PostMapping("/updateByCode") // {{server}}/pallet/updateById
	void updateBySNCode(@RequestBody Pallet pallet) {
		if(pallet==null)
			throw new RestException("未指定更新产品");
		final String code=pallet.getSNCode();
		if(code==null||code.isEmpty())
			throw new RestException("更新失败，未指定SN码");
		Pallet persist=palletRepo.findBySNCode(code);
		if(persist==null)
			throw new RestException("更新失败,无效SN码:"+code);
		ModelUtil.merge(pallet, persist);
		palletRepo.flush();
	}
	
	@GetMapping("/count") // {{server}}/pallet/count?SNCode=
	Long getCount(String SNCode) {
		if(SNCode==null||SNCode.isEmpty())
			return palletRepo.count();
		return palletRepo.getCountBySNCode(SNCode);
	}
	@PostMapping("/get") // {{server}}/pallet/get
	Collection<Pallet> getPallet(@RequestBody Pallet probe){
		if(probe==null)
			throw new RestException("未指定查询条件");
		try {
			Example<Pallet> example=Example.of(probe);
			return palletRepo.findAll(example);
		}catch (Throwable t) {
			log.error("查询失败",t);
			throw new RestException("查询失败");
		}	
	}
	@PostMapping("/inputBox")
	@Transactional
	public boolean inputBox(@RequestBody Pallet pallet) {//栈板装箱,栈板入库并绑定中箱nickName
		//血淋淋的教训,开启事务的方法必需用public来修饰，确保方法访问权限
		String nickName=pallet.getNickName();
		if(nickName==null||nickName.isBlank())
			throw new RestException("栈板未指定SN码(NickName)");
		List<String> boxNickNames=pallet.getBoxNickNames(); 
		if(boxNickNames==null||boxNickNames.isEmpty()) 
			throw new RestException("未指定中箱SN码");		
		Pallet rs=palletRepo.save(pallet);
		int bindCount=cartoonBoxRepo.bindPallet(rs.getNickName(), boxNickNames);//绑定中箱数
		if(bindCount!=boxNickNames.size())
			throw new RestException("可能存在未入库或者已装栈的中箱");		
		return true;
	}


	@Autowired
	private ProductLaserRepo productLaserRepo;


	@PostMapping("/bindShip")// {{server}}/pallet/bindShip?shipCode=xx  [boxCode1,...]
	int bindShip(String shippingCode, String shippingDate, @RequestBody Pallet pallet) {//出货单号批量绑定中箱
		if(shippingCode==null)
			throw new RestException("缺少参数shippingCode");
		if(shippingDate==null)
			throw new RestException("缺少参数shippingDate");
		if(pallet==null)
			throw new RestException("body未指定pallet");

        List<String> ans = new ArrayList<String>();

        String palletNickName = pallet.getNickName();

        List<CartoonBox> boxs = cartoonBoxRepo.findByPalletCode(palletNickName, Sort.by("streamCode"));

        for (int i=0; i<boxs.size(); i++){
            String boxNickName = boxs.get(i).getNickName();
            List<ProductLaser> lasers = productLaserRepo.findByCartoonBoxCode(boxNickName, Sort.by("streamCode"));
            List<String> list = lasers.stream().map(j-> j.getSNCode()).distinct().collect(Collectors.toList());
            ans.addAll(list);
        }
		return productLaserRepo.bindShip(shippingCode, shippingDate, ans);//产品SN码可能未入库/产品的中箱编码可能被覆盖/中箱编码可能未入库
	}

}
