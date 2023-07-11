package com.kwanhor.trace.server.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductInfo extends AbstractProductInfo{
	private String length;//产品长度
	private String width;//产品宽度
	private String height;//产品高度


	private String boxLength;//产品长度
	private String boxWidth;//产品宽度
	private String boxHeight;//产品高度

    private String factoryCode; // 工厂代码
    private String boxQuantity; //装箱数量
    private String boxNetWeight; //中箱净重
    private String boxGrossWeight; //中箱毛重

    private String printerTemplate; //打印模板
}
