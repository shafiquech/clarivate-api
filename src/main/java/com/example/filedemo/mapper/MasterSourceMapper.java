package com.example.filedemo.mapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clarivate.scraping.util.Util;
import com.example.filedemo.entities.MasterSource;
import com.example.filedemo.repo.MasterSourceRepo;
import com.example.filedemo.vo.MasterSourceVO;
import com.example.filedemo.vo.MonitoringSourceVO;
import com.example.filedemo.vo.ScrapperLogVO;
import com.example.filedemo.vo.SourceActionVO;

@Component
public class MasterSourceMapper implements Mapper<MasterSourceVO, MasterSource> {
	
	@Autowired 
	MonitoringSourceMapper monitoringSourceMapper;
	@Autowired 
	ScrapperLogMapper scrapperLogMapper;
	@Autowired
	MasterSourceRepo masterSourceRepo;

	@Override
	public MasterSourceVO mapToVo(MasterSource e) {
		MasterSourceVO vo = new MasterSourceVO();

		vo.seteDate(e.getEDate());
		vo.setFrequency(e.getFrequency());
		vo.setsDate(e.getSDate());
		vo.setTime(e.getTime());
		vo.setTitle(e.getTitle());
		vo.setType(e.getType());
		vo.setUrl(e.getUrl());
		vo.setCountry(e.getCountry());
		vo.setMasterId(e.getMasterId());
		vo.setExecution(e.getExecution());
		vo.setUpdatedDate(e.getUpdatedDate());
		vo.setKeywords(e.getKeywords());
		// emils
		List<String> emails = e.getSourceNotifications().stream().map(x -> x.getEmailaddress())
				.collect(Collectors.toList());
		vo.setNotificationEmails(emails);
		
		// MonitoringSources
		List<MonitoringSourceVO> mos = e.getMonitoringSources().stream().map(monitoringSourceMapper::mapToVo)
				.collect(Collectors.toList());
		vo.setMonitoringSources(mos);
		
		//SourceAction
		List<SourceActionVO> sourceAction = e.getSourceActions().stream()
		.map(x -> new SourceActionVO(x.getActionId(), x.getStepSeq(), x.getUrl()))
		.collect(Collectors.toList());
		vo.setSourceActions(sourceAction);
		
		//Scrapper Log
		List<ScrapperLogVO> scrapperLog = e.getScrapperLogs().stream()
				.map(scrapperLogMapper::mapToVo)
				.collect(Collectors.toList());
		vo.setScrapperLogs(scrapperLog);

		return vo;

	}

	@Override
	public MasterSource mapToEntity(MasterSourceVO v) {

		MasterSource e = new MasterSource();
		// if ID exists it will be an update case
		if (v.getMasterId() > 0) {
			e = masterSourceRepo.findById(v.getMasterId()).get();
		}
		//update date will always be set
		e.setUpdatedDate(new Date());

		if (!Util.isNull(v.geteDate())) {
			e.setEDate(v.geteDate());
		}
		if (!Util.isNull(v.getFrequency())) {
			e.setFrequency(v.getFrequency());
		}
		if (!Util.isNull(v.getsDate())) {
			e.setSDate(v.getsDate());
		}
		if (!Util.isNull(v.getTime())) {
			e.setTime(v.getTime());
		}
		if (!Util.isNull(v.getTitle())) {
			e.setTitle(v.getTitle());
		}
		if (!Util.isNull(v.getType())) {
			e.setType(v.getType());
		}
		if (!Util.isNull(v.getUrl())) {
			e.setUrl(v.getUrl());
		}
		if (!Util.isNull(v.getCountry())) {
			e.setCountry(v.getCountry());
		}
		if (!Util.isNull(v.getExecution())) {
			e.setExecution(v.getExecution());
		}
		if (!Util.isNull(v.getKeywords())) {
			e.setKeywords(v.getKeywords());
		}

		return e;

	}
	
	

	// custom mappers
	
	public MasterSourceVO mapWithMonitoring(MasterSource e) {
		MasterSourceVO vo = new MasterSourceVO();

		vo.seteDate(e.getEDate());
		vo.setFrequency(e.getFrequency());
		vo.setsDate(e.getSDate());
		vo.setTime(e.getTime());
		vo.setTitle(e.getTitle());
		vo.setType(e.getType());
		vo.setUrl(e.getUrl());
		vo.setCountry(e.getCountry());
		vo.setMasterId(e.getMasterId());
		vo.setExecution(e.getExecution());
		// MonitoringSources
		List<MonitoringSourceVO> mos = e.getMonitoringSources().stream().map(monitoringSourceMapper::mapToVo)
				.collect(Collectors.toList());

		vo.setMonitoringSources(mos);

		return vo;

	}

}
