package com.kwanhor.trace.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 出货信息
 * @author LiangGuanHao
 *
 */
@Data
public class Shipping {
	private String productPartNumber;//产品料号
	private String productType; //产品类型号码
	private String productSN; //产品SN,多个用逗号隔开
	/**
	 * 中箱nickName
	 */
	private String cartoonBoxSN; //卡通箱SN(疑似冗余信息，先忽略)
	/**
	 * 栈板nickName
	 */
	private String palletSN; //栈板SN
	private String createDate; // 生产日期
	private String shippingDate; // 出货日期
	private String shippingCode; //出货单号
	private String creator;//创建人
	@JsonProperty(value = "SKU")
	private String SKU;
	/**
	 * 
	 * @param productLaser 出货产品
	 * @return 出货信息(不含栈板SN)
	 */
	public static Shipping fromProductLaser(ProductLaser productLaser) {
		Shipping shipping=new Shipping();
		shipping.setProductPartNumber(productLaser.getProductPartNumber());
		shipping.setProductType(productLaser.getProductType());
		shipping.setProductSN(productLaser.getSNCode());
		shipping.setCartoonBoxSN(productLaser.getCartoonBoxCode());
		shipping.setCreateDate(productLaser.getCreateDate());
		shipping.setShippingDate(productLaser.getShippingDate());
		shipping.setShippingCode(productLaser.getShippingCode());
		shipping.setCreator(productLaser.getCreator());
		shipping.setSKU(productLaser.getSKU());
		return shipping;
	}
	/**
	 * 中箱出货信息
	 * @param box 出货中箱
	 * @param laserSNs 产品SN码，多个用逗号隔开
	 * @param laserShippingDate 出货日期，同一批中箱出货的产品记录的出货日期一致
	 * @param laserShippingCode 出货单号，同一批中箱出货的产品记录的出货单号一致
	 * @return 中箱出货信息
	 */
	public static Shipping fromBox(CartoonBox box,String laserSNs,String laserShippingDate,String laserShippingCode) {
		Shipping shipping=new Shipping();
		shipping.setProductPartNumber(box.getProductPartNumber());
		shipping.setProductType(box.getProductType());
		shipping.setProductSN(laserSNs);
		shipping.setCartoonBoxSN(box.getSNCode());
		shipping.setCreateDate(box.getCreateDate());
		shipping.setShippingDate(laserShippingDate);
		shipping.setShippingCode(laserShippingCode);
		shipping.setCreator(box.getCreator());
		shipping.setSKU(box.getSKU());
		shipping.setPalletSN(box.getPalletCode());
		return shipping;
	}
}
