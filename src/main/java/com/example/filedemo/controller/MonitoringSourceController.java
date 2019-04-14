package com.example.filedemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.filedemo.service.MasterSourceService;
import com.example.filedemo.service.MonitoringSourceService;
import com.example.filedemo.vo.MasterSourceVO;
import com.example.filedemo.vo.MonitoringSourceVO;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class MonitoringSourceController {
	
	@Autowired
	MonitoringSourceService monitoringSourceService;
	
	@Autowired
	MasterSourceService masterSourceService;

	
	@GetMapping("monitoringsource")
	@ResponseBody
	public List<MonitoringSourceVO> getAllMonitoringsource() {

		return monitoringSourceService.getAllMonitoringsource();
	}
	
	@GetMapping("mastersource_monitoring")
	@ResponseBody
	public List<MasterSourceVO> getMasterSourceMonitoring() {

		return masterSourceService.getAllMonitoringsource();
	}
	
	@PostMapping("monitoringsource")
	@ResponseBody
	public String saveMonitoringSource(@RequestBody MonitoringSourceVO monitoringSourceVO) {

		return monitoringSourceService.saveMonitoringSource(monitoringSourceVO);
	}
}
