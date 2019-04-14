package com.example.filedemo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.filedemo.entities.SourceAction;

public interface SourceActionRepo extends JpaRepository<SourceAction, Integer> {

	public List<SourceAction> findByMasterSource_MasterId(int masterId);
	
	public void deleteByMasterSource_MasterId(int masterId);
}
