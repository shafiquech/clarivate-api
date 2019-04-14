package com.example.filedemo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.filedemo.entities.ScrapperLog;
import com.example.filedemo.mapper.ScrapperLogMapper;
import com.example.filedemo.repo.ScrapperLogRepo;
import com.example.filedemo.vo.ScrapperLogVO;

@Service
@Transactional
public class ScrapperLogService {

	@Autowired
	ScrapperLogRepo scapperLogRepo;
	@Autowired
	ScrapperLogMapper scrapperLogMapper;

	public List<ScrapperLogVO> getAllScrapperLog() {
		return scapperLogRepo.findAll().stream().map(scrapperLogMapper::mapToVo).collect(Collectors.toList());
	}

	public String updateScrapperLog(Integer scrapperId, String url) {
	
		Optional<ScrapperLog> logOptioal = scapperLogRepo.findById(scrapperId);
		if(logOptioal.isPresent())
		{
			ScrapperLog log = logOptioal.get();
			log.setDatetime(new Date());
			log.setGenerateDiffFile(url);
			scapperLogRepo.save(log);
		}else {
			return "Log not found for Scrapper "+scrapperId;
		}
		
		return "Ok";
	}

	public String acceptChanges(Integer scrapperId, String filename) {
		scapperLogRepo.deleteById(scrapperId);
		return "Ok";
	}

	public String rejectChanges(Integer scrapperId, String filename) {
		scapperLogRepo.deleteByFileName(filename);
		return "Ok";
	}

}
