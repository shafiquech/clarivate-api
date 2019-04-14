package com.example.filedemo.mapper;

import org.springframework.stereotype.Component;

import com.example.filedemo.entities.ScrapperLog;
import com.example.filedemo.vo.ScrapperLogVO;

@Component
public class ScrapperLogMapper implements Mapper<ScrapperLogVO, ScrapperLog> {

	@Override
	public ScrapperLogVO mapToVo(ScrapperLog e) {
		ScrapperLogVO vo = new ScrapperLogVO();
		vo.setDatetime(e.getDatetime());
		vo.setDescription(e.getDescription());
		vo.setFileName(e.getFileName());
		vo.setPath(e.getPath());
		vo.setScrapperId(e.getScrapperId());
		vo.setGenerateDiffFile(e.getGenerateDiffFile());
		vo.setMasterId(e.getMasterSource().getMasterId());
		return vo;

	}

	@Override
	public ScrapperLog mapToEntity(ScrapperLogVO v) {
		// TODO Auto-generated method stub
		return null;
	}

}
