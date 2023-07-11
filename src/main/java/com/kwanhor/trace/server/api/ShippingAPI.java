package com.kwanhor.trace.server.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.CartoonBox;
import com.kwanhor.trace.server.model.Pallet;
import com.kwanhor.trace.server.model.ProductLaser;
import com.kwanhor.trace.server.model.Shipping;
import com.kwanhor.trace.server.repo.CartoonBoxRepo;
import com.kwanhor.trace.server.repo.PalletRepo;
import com.kwanhor.trace.server.repo.ProductLaserRepo;

@RestController
@RequestMapping("/shipping")
public class ShippingAPI {
	@Autowired
	private CartoonBoxRepo cartoonBoxRepo;
	@Autowired
	private ProductLaserRepo productLaserRepo;
	@Autowired
	private PalletRepo palletRepo;
	/**
	 * 中箱出货接口
	 * @param SNCode 中箱nickName
	 * @return 中箱对应的所有出货数据
	 */
	@PostMapping("/byBox")// {{server}}/shipping/byBox?SNCode=
	Shipping byBox(String SNCode) {//按中箱生成出货信息
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("出货中箱SN码不能为空");
		CartoonBox box=cartoonBoxRepo.findByNickName(SNCode);
		if(box==null)
			throw new RestException("出货中箱("+SNCode+")未入库");
		List<ProductLaser> lasers=productLaserRepo.findByCartoonBoxCode(SNCode, Sort.by("SNCode").ascending());//按SN码升序排列
		if(lasers==null||lasers.isEmpty())
			throw new RestException("出货中箱("+SNCode+")未放置产品");
		ProductLaser firstLaser=lasers.get(0);
		String shippingDate=firstLaser.getShippingDate();
		String shippingCode=firstLaser.getShippingCode();
		StringBuilder laserSNs=new StringBuilder();
		laserSNs.append(firstLaser.getSNCode());
		for(int i=1;i<lasers.size();i++) {
			laserSNs.append(",").append(lasers.get(i).getSNCode());			
		}
		return Shipping.fromBox(box, laserSNs.toString(), shippingDate, shippingCode);		
	}
	/**
	 * 栈板出货接口
	 * @param SNCode 栈板nickName
	 * @return 栈板的出货信息
	 */
	@PostMapping("/byPallet")// {{server}}/shipping/byPallet?SNCode=
	Shipping byPallet(String SNCode) {//按中箱生成出货信息
		if(SNCode==null||SNCode.isEmpty())
			throw new RestException("出货栈板SN码不能为空");
		Pallet pallet=palletRepo.findByNickName(SNCode);
		if(pallet==null)
			throw new RestException("出货栈板("+SNCode+")未入库");
		List<CartoonBox> boxList=cartoonBoxRepo.findByPalletCode(SNCode, Sort.by("SNCode").ascending());
		if(boxList==null||boxList.isEmpty())
			throw new RestException("出货栈板("+SNCode+")未放置中箱");
		final List<String> boxCodes=new ArrayList<>();
		boxList.forEach(box->boxCodes.add(box.getNickName()));
		List<ProductLaser> lasers=productLaserRepo.findByCartoonBoxCodeIn(boxCodes, Sort.by("SNCode").ascending());//按SN码升序排列
		if(lasers==null||lasers.isEmpty())
			throw new RestException("出货栈板("+SNCode+")未放置产品");
		ProductLaser firstLaser=lasers.get(0);
		String shippingDate=firstLaser.getShippingDate();
		String shippingCode=firstLaser.getShippingCode();
		StringBuilder laserSNs=new StringBuilder();
		laserSNs.append(firstLaser.getSNCode());
		for(int i=1;i<lasers.size();i++) {
			laserSNs.append(",").append(lasers.get(i).getSNCode());			
		}
		return Shipping.fromBox(boxList.get(0), laserSNs.toString(), shippingDate, shippingCode);		
	}
}
