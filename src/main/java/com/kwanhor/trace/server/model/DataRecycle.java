package com.kwanhor.trace.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"keyName","keyValue"}),indexes = {@Index(columnList = "fqcn")})
public class DataRecycle {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@CreatedDate
	@Column(insertable = true,updatable = false)
	private Date createTime;//记录创建日期	
	@LastModifiedDate
	@Column(insertable = false,updatable = true)
	private Date updateTime;//记录修改日期
	private String fqcn;//数据类FQCN
	@Column(columnDefinition = "Text")
	private String data;//数据JSON格式
	private String keyName;//唯一索引字段名
	private String keyValue;//唯一索引字段值
}
