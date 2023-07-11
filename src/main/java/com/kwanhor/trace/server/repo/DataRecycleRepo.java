package com.kwanhor.trace.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwanhor.trace.server.model.DataRecycle;

public interface DataRecycleRepo extends JpaRepository<DataRecycle, Long> {
	
}
