package com.example.filedemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.filedemo.entities.ScrapperLog;

@Repository
public interface ScrapperLogRepo extends JpaRepository<ScrapperLog, Integer> {

	void deleteByMasterSource_MasterId(int masterId);
	
	@Modifying
	@Query("delete from ScrapperLog u where u.fileName like ?1%")
	void deleteByFileName(String filename);

}
