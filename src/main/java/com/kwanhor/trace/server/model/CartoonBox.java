package com.kwanhor.trace.server.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "SNCode"),@UniqueConstraint(columnNames = "nickName")},indexes = {@Index(columnList = "palletCode"),@Index(columnList = "createDate")})
public class CartoonBox extends AbstractProductInfo{
	@Column(length = 32,nullable = false)
	private String createDate;//生产日期，格式yyyyMMdd
	@Column(length = 64)
	private String creator;//创建人;例如:陈华友
	@Column(length = 16)
	private String factoryCode;// 工厂编码;例如:0B
	@Column(length = 64)
	private String quantity;//装载数量;例如:100PCS
	@Column(length = 16)
	private String boxLength;// 箱子长度;例如:1m
	@Column(length = 16)
	private String boxWidth;//箱子宽度;例如:2m
	@Column(length = 16)
	private String boxHeight;// 箱子高度;例如:1m
	@Column(length = 16)
	private String boxNetWeight;//箱子净重;例如:0.1KG
	@Column(length = 16)
	private String boxGrossWeight;// 箱子毛重;例如:5KG
	@Column(length = 32)
	private String status;// 可选值:正常使用/退回维修;例如:正常使用
	private Long streamCode;//流水号十进制值
	@JsonProperty("SNCode")
	@Column(unique = true,nullable = false,length = 64)
	private String SNCode;// 卡通箱SN码;例如:CX3PROWH0BOK7N00001
	/**
	 * 栈板nickName
	 */
	@Column(length = 64)
	private String palletCode;//栈板编码
	@Column(unique = true,nullable = false,length = 64)
	private String nickName;
	
	private String supplier; //供应商
	private String prodPlace; //生产地
	private String salesArea; //销售区域
	private String pkgSpec; // 包装规格
	@JsonProperty("PO")
	private String PO; // 采购订单号
	private String inspection; // 检验员/检验批次
	private String volume; // 体积
	private String dstCity; // 目的城市
	private String dstStoreHouse; // 目的仓
	@Transient
	private List<String> laserSNCodes;//中箱装箱的产品SN码
}