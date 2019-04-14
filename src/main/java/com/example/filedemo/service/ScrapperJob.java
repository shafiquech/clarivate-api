package com.example.filedemo.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.clarivate.scraping.util.Constants;
import com.clarivate.scraping.util.Util;
import com.example.filedemo.entities.MasterSource;
import com.example.filedemo.repo.MasterSourceRepo;

@Service
public class ScrapperJob {

	@Autowired
	MasterSourceRepo masterSourceRepo;
	@Autowired
	MasterSourceService masterSourceService;

	@Scheduled(fixedRate = 60000)
	public void reportCurrentTime() {

		Date currentTime = Util.getTime(Util.getCurrentTime());

	 List<MasterSource> runableMasters =	masterSourceRepo
			 								.getRunnableJobs(new Date())
											.stream()
											.filter(x -> !Util.isNull(x.getTime()))
											.filter(x -> Constants.JOB_SCHEDULE_DAILY.equalsIgnoreCase(x.getFrequency()))
											.filter(x -> currentTime.compareTo(Util.getTime(x.getTime())) == 0)
											.collect(Collectors.toList());
	 
	for (MasterSource masterSource : runableMasters) {
		
		System.out.println(masterSource.getMasterId() + " ScrapperJob  has started ... " + new Date());
		
		masterSourceService.mastersource_download(masterSource.getMasterId());
		
		System.out.println(masterSource.getMasterId() + " ScrapperJob  has ended ... " + new Date());
	}
	 
	 
	}

}
