package com.example.filedemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.filedemo.entities.MonitoringSource;

@Repository
public interface MonitoringSourceRepo extends JpaRepository<MonitoringSource, Integer> {

	void deleteByMasterSource_MasterId(int masterId);

}
