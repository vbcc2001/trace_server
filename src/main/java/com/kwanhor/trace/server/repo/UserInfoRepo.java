package com.kwanhor.trace.server.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwanhor.trace.server.model.UserInfo;

public interface UserInfoRepo extends JpaRepository<UserInfo, Long>{
	List<UserInfo> getByUserName(String userName);
}
