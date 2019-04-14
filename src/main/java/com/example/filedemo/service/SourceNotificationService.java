package com.example.filedemo.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.filedemo.repo.SourceNotificationRepo;

@Service
@Transactional
public class SourceNotificationService {
	
	@Autowired
	SourceNotificationRepo sourceNotificationRepo;

}
