package com.example.filedemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.filedemo.service.ScrapperLogService;
import com.example.filedemo.vo.ScrapperLogVO;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class ScrapperLogController {
	
	@Autowired
	ScrapperLogService scrapperLogService;

	@GetMapping("scrapperLog")
	@ResponseBody
	public List<ScrapperLogVO> getAllScrapperLog() {

		return scrapperLogService.getAllScrapperLog();
	}
	@PostMapping("scrapperLog")
	@ResponseBody
	public String updateScrapperLog(@RequestParam Integer scrapperId, @RequestParam String filename) {

		return scrapperLogService.updateScrapperLog(scrapperId, filename);
	}
	
	@GetMapping("scrapperLog/accept/{scrapperId}")
	@ResponseBody
	public String acceptChanges(@PathVariable Integer scrapperId,  @RequestParam String filename) {

		return scrapperLogService.acceptChanges(scrapperId, filename);
	}
	@GetMapping("scrapperLog/reject/{scrapperId}")
	@ResponseBody
	public String rejectChanges(@PathVariable Integer scrapperId,  @RequestParam String filename) {

		return scrapperLogService.rejectChanges(scrapperId, filename);
	}
}
