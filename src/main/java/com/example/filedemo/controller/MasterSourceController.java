package com.example.filedemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.filedemo.service.MasterSourceService;
import com.example.filedemo.vo.MasterSourceVO;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class MasterSourceController {

	@Autowired
	MasterSourceService masterSourceService;

	@GetMapping("mastersource")
	@ResponseBody
	public List<MasterSourceVO> getAllMasterSource() {

		return masterSourceService.getAllMasterSource();
	}

	@GetMapping("mastersource/{masterId}")
	@ResponseBody
	public MasterSourceVO getMasterSource(@PathVariable Integer masterId) {

		return masterSourceService.getMasterSourceById(masterId);
	}

	@PostMapping("mastersource")
	public MasterSourceVO saveSource(@RequestBody MasterSourceVO masterSourceVO) {

		return masterSourceService.saveMasterData(masterSourceVO);

	}
	@DeleteMapping("mastersource/{masterId}")
	@ResponseBody
	public String deleteMasterSource(@PathVariable Integer masterId) {

		return masterSourceService.deleteMasterSource(masterId);
	}
	
	@PostMapping("mastersourceBulk")
	public String saveSourceBulk(@RequestBody MasterSourceVO masterSourceVO) {

		return masterSourceService.saveSourceBulk(masterSourceVO);

	}
	
	@GetMapping("sourcenotification/{masterId}")
	@ResponseBody
	public List<String> getSourceNotification(@PathVariable Integer masterId) {

		return masterSourceService.getMasterSourceById(masterId).getNotificationEmails();
	}
	
	@GetMapping("mastersource_download/{masterId}")
	@ResponseBody
	public String mastersource_download(@PathVariable Integer masterId, @RequestParam String fileType) {

		return masterSourceService.mastersource_download(masterId);
	}


}
