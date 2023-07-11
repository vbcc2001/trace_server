package com.kwanhor.trace.server.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kwanhor.trace.server.init.DictionaryManager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) //抽象类，不会自动建表, 就算建了也不可用
public abstract class AbstractProductInfo extends BaseEntity{
	private String productPartNumber;//产品料号cs0001
	private String productName;//产品名称 20D561K-J-P10-L3.5
	private String productType;//产品类型 S20D561K98035L10高焦耳-NDF
	private String productColor;//产品颜色
	private String productId;//产品ID,由客户自定义
	private String code69;//69码
	@JsonProperty(value = "SKU")
	private String SKU;
	@JsonProperty(value = "projectSPU")
	private String projectSPU;	
}