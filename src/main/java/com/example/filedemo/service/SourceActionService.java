package com.example.filedemo.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.filedemo.vo.SourceActionVO;
import com.example.filedemo.repo.SourceActionRepo;

@Service
@Transactional
public class SourceActionService {

	@Autowired
	SourceActionRepo sourceActionRepo;

	public List<SourceActionVO> getSourceAction(Integer masterId) {

		return sourceActionRepo.findByMasterSource_MasterId(masterId)
				.stream()
				.map(x -> new SourceActionVO(x.getActionId(), x.getStepSeq(), x.getUrl()))
				.collect(Collectors.toList());
	}

}
