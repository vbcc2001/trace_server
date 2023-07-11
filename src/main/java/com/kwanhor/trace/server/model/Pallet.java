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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "SNCode"),@UniqueConstraint(columnNames = "nickName")},indexes = {@Index(columnList = "PO"),@Index(columnList = "SNCode"),@Index(columnList = "createDate")})
public class Pallet extends AbstractProductInfo{//托盘
	@Column(length = 32,nullable = false)
	private String createDate;//生产日期，格式yyyyMMdd
	@Column(length = 32)
	private String creator;//创建人;例如:陈华友
	@Column(length = 16)
	private String factoryCode;// 工厂编码;例如:0B
	@Column(length = 16)
	private String workshopCode;// 车间编码;例如:0A
	@Column(length = 64)
	private String quantity;//装板数量;例如:100PCS
	@Column(length = 128)
	private String fromArea;// 出货地;例如:广州
	@Column(length = 128)
	private String toArea;//目的地;例如:深圳
	private Long streamCode;//流水号十进制值
	@JsonProperty("SNCode")
	@Column(unique = true,nullable = false,length = 64)
	private String SNCode;//栈板SN码;例如:PX3PROWH0BOK7001
	@JsonProperty("PO")
	private String PO;//采购订单号purchase order
	private String grossWeight;//托盘毛重
	private String nickName;
	/**
	 * 捆绑的中箱nickName,不入库
	 */
	@Transient
	private List<String> boxNickNames;
}