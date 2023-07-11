package com.kwanhor.trace.server.model;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;
/**
 * 产品编码
 * @author LiangGuanHao
 *
 */
@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"prefix","isDay"}),indexes = {@Index(columnList = "prefix"),@Index(columnList = "fqcn")})
public class ProductCode extends BaseEntity{
	private int pos;//最后一次分发的流水号
	private String prefix;//SN码前缀,由前端指定
	private String fqcn;//编码实体对应
	private boolean isDay;//是否为日流水
}
