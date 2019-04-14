package com.example.filedemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.filedemo.service.SourceActionService;
import com.example.filedemo.vo.SourceActionVO;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class SourceActionController {

	@Autowired
	SourceActionService sourceactionService;

	@GetMapping("sourceaction/{masterId}")
	@ResponseBody
	public List<SourceActionVO> getSourceAction(@PathVariable Integer masterId) {

		return sourceactionService.getSourceAction(masterId);
	}

}
