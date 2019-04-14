package com.example.filedemo.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.filedemo.entities.MasterSource;

@Repository
public interface MasterSourceRepo extends JpaRepository<MasterSource, Integer> {
 public List<MasterSource> sDateAfter(Date currentDate);
 public List<MasterSource> findAllByOrderByUpdatedDateDesc();
 
 @Query(value = "from MasterSource t where ?1 BETWEEN t.sDate AND t.eDate and t.execution='Auto' ")
 public List<MasterSource> getRunnableJobs(Date currentDate);
 
}
