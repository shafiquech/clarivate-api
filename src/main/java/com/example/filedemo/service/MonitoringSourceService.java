package com.example.filedemo.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.filedemo.entities.MasterSource;
import com.example.filedemo.entities.MonitoringSource;
import com.example.filedemo.mapper.MonitoringSourceMapper;
import com.example.filedemo.repo.MonitoringSourceRepo;
import com.example.filedemo.vo.MonitoringSourceVO;

@Service
public class MonitoringSourceService {

	@Autowired
	MonitoringSourceRepo monitoringSourceRepoeRepo;
	@Autowired
	MonitoringSourceMapper monitoringSourceMapper;
	@Transactional
	public List<MonitoringSourceVO> getAllMonitoringsource() {

		return monitoringSourceRepoeRepo.findAll().stream().map(monitoringSourceMapper::mapToVo)
				.collect(Collectors.toList());
	}
	@Transactional
	public String saveMonitoringSource(MonitoringSourceVO monitoringSourceVO) {
		MonitoringSource ms = monitoringSourceMapper.mapToEntity(monitoringSourceVO);
		monitoringSourceRepoeRepo.save(ms);
		return "Success";
	}
	@Transactional (propagation = Propagation.REQUIRES_NEW)
	public MonitoringSource saveMsStatus(MasterSource masterSource) {
		MonitoringSource mss = new MonitoringSource();
		mss.setDatetime(new Date());
		mss.setError("Download is running");
		mss.setStatus("Running");
		mss.setMasterSource(masterSource);
		mss = monitoringSourceRepoeRepo.save(mss);
		return mss;
	}

}
