package com.kwanhor.trace.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.kwanhor.trace.server.init.DictionaryManager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"dicType","code"}))
public class Dictionary extends BaseEntity{	
	@Column(length = 128,updatable = false,nullable = false)
	private String dicType;//字典分组
	@Column(length = 128,updatable = false,nullable = false)
	private String code;//字典编码
	@Column(nullable = false,columnDefinition = "TEXT")
	private String text;//字典值,不允许为空
	@Column(columnDefinition = "TEXT")
	private String desc;//字典描述,默认为空	
}
