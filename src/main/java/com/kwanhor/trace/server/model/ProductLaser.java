package com.kwanhor.trace.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "SNCode"),indexes = {@Index(columnList = "cartoonBoxCode"),@Index(columnList = "shippingCode"),@Index(columnList = "createDate")})
public class ProductLaser extends AbstractProductInfo{
	@Column(length = 32,nullable = false)
	private String createDate;//生产日期，格式yyyy-MM-dd
	@Column(length = 64)
	private String creator;//员工姓名
	@Column(length = 16)
	private String factoryCode;//供应商编码
	@JsonProperty("SNCode")
	@Column(unique = true,nullable = false,length = 64)
	private String SNCode;//镭射编码
	private Long streamCode;//流水号十进制值
	private String status;//产品状态
	private String used;//是否已喷码
	/**
	 * 中箱nickName
	 */
	@Column(length = 64)
	private String cartoonBoxCode;//装箱编码(nickName)
	@Column(length = 64)
	private String shippingCode;//出货单号 A00-B123-C0990 为空表示取消出货
	@Column(length = 32)
	private String shippingDate;//出货日期 20220710
	private String ecologicalChain;//生态链公司
	private String reserved;//预留位
	private String productId;
	private String weight;//产品重量
	
}
