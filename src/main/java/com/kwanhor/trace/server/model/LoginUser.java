package com.kwanhor.trace.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LoginUser extends BaseEntity{
	private String name;
	private String pwd;
	@Column(length = 32,nullable = false)
	private String nickName;
	
}
