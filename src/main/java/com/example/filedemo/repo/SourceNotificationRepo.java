package com.example.filedemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.filedemo.entities.SourceNotification;

public interface SourceNotificationRepo extends JpaRepository<SourceNotification, Integer> {
	
	public void deleteByMasterSource_MasterId(int masterId);
}
