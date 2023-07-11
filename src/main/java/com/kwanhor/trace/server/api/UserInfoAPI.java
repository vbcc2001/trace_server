package com.kwanhor.trace.server.api;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.UserInfo;
import com.kwanhor.trace.server.repo.UserInfoRepo;
import com.kwanhor.trace.server.util.ModelUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/userInfo")
@Slf4j
public class UserInfoAPI {
	@Autowired
	private UserInfoRepo userInfoRepo;

    // 在持久层注入
    @PersistenceContext
    EntityManager entityManager;
    /**
     * 用户登录<br>
     * 请求地址: {{server}}/userInfo/login?name=xx&pwd=xx
     * @param name 用户名
     * @param pwd 用户密码
     * @return 用户信息
     * @throws RestException 用户名/密码为空;用户名不存在;用户已冻结;密码错误;
     */
    UserInfo login(String name,String pwd) {
    	if(name==null||name.isBlank())
    		throw new RestException("用户名不能为空");
    	if(pwd==null||pwd.isBlank())
    		throw new RestException("密码不能为空");
    	List<UserInfo> list=userInfoRepo.getByUserName(name);
    	if(list==null||list.isEmpty())
    		throw new RestException("用户名不存在:"+name);
    	UserInfo user=list.get(0);
    	if(user.isFrozen())
    		throw new RestException("用户已冻结");
    	if(pwd.equals(user.getUserPasswd())) {
    		return user;
    	}
    	throw new RestException("密码错误");
    }
    /**
     * 新增一个用户 <br>
     * 请求地址: {{server}}/userInfo/add
     * @param productLaserList 需要添加的用户信息(不含ID)
     * @return 已入库的用户信息(含ID)
     * @throws RestException 缺少用户名/密码;用户名重复
     */
	@PostMapping("/add")
	UserInfo addUser(@RequestBody UserInfo userInfo) {
		checkEmptyUser(userInfo);
		checkRepeat(userInfo.getUserName());
		return userInfoRepo.save(userInfo);
	}
	private void checkEmptyUser(UserInfo userInfo) {
		if(userInfo==null)
			throw new RestException("未指定用户信息");
		String userName=userInfo.getUserName();
		if(userName==null||userName.isBlank())
			throw new RestException("用户名不能为空");
		String pwd=userInfo.getUserPasswd();
		if(pwd==null||pwd.isBlank())
			throw new RestException("密码不能为空");
	}
	private void checkRepeat(String userName) {
		List<UserInfo> list=userInfoRepo.getByUserName(userName);
		if(list!=null&&list.size()>0) 
			throw new RestException("已有同名用户:"+userName);
	}
	/**
	 * 按ID更新用户.<br>
	 * 请求地址: {{server}}/userInfo/updateById
	 * @param userInfo 更新后的用户信息,必须包含ID
	 * @throws RestException 缺少ID/用户名/密码;用户名重复;修改用户状态;
	 */
	@PostMapping("/updateById")
	UserInfo updateById(@RequestBody UserInfo userInfo) {
		checkEmptyUser(userInfo);
		final Long id=userInfo.getId();
		if(id==null)
			throw new RestException("更新失败，未指定用户ID");
		UserInfo persist=userInfoRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,用户ID不存在:"+id);
		String originName=persist.getUserName();
		String userName=userInfo.getUserName();
//		if(persist.isFrozen()!=userInfo.isFrozen()) {//禁止修改用户状态
//			throw new RestException("更新失败,不能修改用户状态");
//		}
		if(!originName.equals(userName)) {//修改用户名
			checkRepeat(userName);//防止重名
		}
		ModelUtil.merge(userInfo, persist);
		userInfoRepo.flush();
		return persist;
	}
	/**
	 * 按用户ID逻辑删除用户信息(用户状态标记为已冻结);<br>
	 * 请求地址: {{server}}/userInfo/delById
	 * @param ids 用户ID
	 * @throws RestException id为空
	 */
	@PostMapping("/delById")
	void delById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要冻结的用户ID");	
		List<UserInfo> list=userInfoRepo.findAllById(ids);
		for(UserInfo userInfo:list) {
			userInfo.setFrozen(true);
		}
		userInfoRepo.flush();
	}
	/**
	 * 按ID批量激活用户<br>
	 * 请求地址: {{server}}/userInfo/activeById
	 * @param ids 用户ID
	 * @throws RestException id为空
	 */
	@PostMapping("/activeById")
	void activeById(@RequestBody List<Long> ids) {
		if(ids==null||ids.isEmpty())
			throw new RestException("未指定需要激活的用户ID");	
		List<UserInfo> list=userInfoRepo.findAllById(ids);
		for(UserInfo userInfo:list) {
			userInfo.setFrozen(false);
		}
		userInfoRepo.flush();
	}
	/**
	 * 查询所有符合模型的用户信息<br>
	 * 请求地址: {{server}}/userInfo/get
	 * @param probe 包含查询条件的用户信息模型
	 * @return 查询结果
	 */
	@PostMapping("/get")
	Collection<UserInfo> get(@RequestBody UserInfo probe){
		if(probe==null)
			throw new RestException("未指定查询条件");
		try {
			Example<UserInfo> example=Example.of(probe);
			return userInfoRepo.findAll(example);
		}catch (Throwable t) {
			log.error("查询失败",t);
			throw new RestException("查询失败");
		}	
	}
	/**
	 * 通用查询<br>
	 * 请求地址: {{server}}/userInfo/getByExtendSql?extendSql=&pageIndex=0&pageSize=10
	 * @param extendSql 扩展SQL查询条件
	 * @param pageIndex 分页索引,从0开始.小于0表示查询所有
	 * @param pageSize 分页大小,默认为30
	 * @return 查询结果集
	 */
	@GetMapping("/getByExtendSql")
	List<UserInfo> getByExtendSql(String extendSql,int pageIndex,int pageSize) {
		if(extendSql==null||extendSql.isEmpty())
			throw new RestException("extendSql 不能为空");
		String sql = "select * from UserInfo where "+extendSql;
		TypedQuery<UserInfo> query=entityManager.createQuery(sql,UserInfo.class);
		if(pageIndex>=0) {//分页查询
			if(pageSize<=0)pageSize=30;
			query.setFirstResult(pageIndex*pageSize);
			query.setMaxResults(pageSize);
		}
		return query.getResultList();       
	}

	@PostMapping("/getAll")
	Iterable<UserInfo> getAll(){
		return userInfoRepo.findAll();
	}

	@GetMapping("/count")
	long getCount() {
		return userInfoRepo.count();
	}


}
