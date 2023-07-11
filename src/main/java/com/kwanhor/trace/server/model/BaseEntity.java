package com.kwanhor.trace.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(value = {AuditingEntityListener.class})
//@Table(indexes = @Index(name = "index_del",columnList = "isDeleted"))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"}) //动态代理实体类序列化时会包含一些特殊属性需要屏蔽
public abstract class BaseEntity {//抽象类，不会自动建表, 就算建了也不可用
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
//	@JsonIgnore
//	@Column(nullable = true)
//	private Boolean isDeleted;//是否已删除
	@JsonIgnore
	@CreatedDate
	@Column(insertable = true,updatable = false)
	private Date createTime;//记录创建日期
	@JsonIgnore
	@LastModifiedDate
	@Column(insertable = false,updatable = true)
	private Date updateTime;//记录修改日期
	
//	public boolean isDeleted() {
//		return isDeleted==Boolean.TRUE;
//	}
}
