package com.kwanhor.trace.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "userName"))
public class UserInfo {
	/**
	 * 账号名称,不允许重复
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String userName;
    private String userPasswd; //用户密码
    private String colorBox = "否"; //彩盒权限默认否,下面的字段类似
    private String weight = "否"; //彩盒权限默认否,下面的字段类似
    private String cartoonBox = "否"; //彩盒权限默认否,下面的字段类似
    private String pallet = "否"; //彩盒权限默认否,下面的字段类似
    private String shipping = "否"; //彩盒权限默认否,下面的字段类似
    private String product = "否"; //彩盒权限默认否,下面的字段类似
    private String laser = "否"; //彩盒权限默认否,下面的字段类似
    private String mygroup = "生产单位"; //彩盒权限默认否,下面的字段类似
    private boolean isFrozen;//用户是否已冻结
}
