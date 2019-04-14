package com.example.filedemo.mapper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.filedemo.entities.MasterSource;
import com.example.filedemo.entities.MonitoringSource;
import com.example.filedemo.repo.MasterSourceRepo;
import com.example.filedemo.vo.MonitoringSourceVO;

@Component
public class MonitoringSourceMapper implements Mapper<MonitoringSourceVO, MonitoringSource> {
	@Autowired
	private MasterSourceRepo masterSourceRepo;

	@Override
	public MonitoringSourceVO mapToVo(MonitoringSource e) {
		MonitoringSourceVO vo = new MonitoringSourceVO();
		vo.setDatetime(e.getDatetime());
		vo.setError(e.getError());
		vo.setLogId(e.getLogId());
		vo.setStatus(e.getStatus());
		return vo;
	}

	@Override
	public MonitoringSource mapToEntity(MonitoringSourceVO v) {
		MonitoringSource ms = new MonitoringSource();
		ms.setDatetime(v.getDatetime());
		ms.setError(v.getError());
		ms.setStatus(v.getStatus());
		Optional<MasterSource> masterSource = masterSourceRepo.findById(v.getMasterId());
		if (masterSource.isPresent()) {
			ms.setMasterSource(masterSource.get());
		}

		return ms;
	}

}
