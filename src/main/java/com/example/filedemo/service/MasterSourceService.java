package com.example.filedemo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarivate.scraping.core.ScrapingNews;
import com.clarivate.scraping.util.Util;
import com.example.filedemo.entities.MasterSource;
import com.example.filedemo.entities.MonitoringSource;
import com.example.filedemo.entities.ScrapperLog;
import com.example.filedemo.entities.SourceAction;
import com.example.filedemo.entities.SourceNotification;
import com.example.filedemo.mapper.MasterSourceMapper;
import com.example.filedemo.property.FileStorageProperties;
import com.example.filedemo.repo.MasterSourceRepo;
import com.example.filedemo.repo.MonitoringSourceRepo;
import com.example.filedemo.repo.ScrapperLogRepo;
import com.example.filedemo.repo.SourceActionRepo;
import com.example.filedemo.repo.SourceNotificationRepo;
import com.example.filedemo.vo.MasterSourceVO;

@Service
@Transactional
public class MasterSourceService {

	@Autowired
	MasterSourceRepo masterSourceRepo;
	@Autowired
	MasterSourceMapper masterSourceMapper;
	@Autowired
	SourceNotificationRepo sourceNotificationRepo;
	@Autowired
	SourceActionRepo sourceActionRepo;
	@Autowired
	FileStorageProperties fileStorageProperties;
	@Autowired
	ScrapperLogRepo scrapperLogRepo;
	@Autowired
	MonitoringSourceRepo monitoringSourceRepoeRepo;
	@Autowired
	MonitoringSourceService monitoringSourceService;

	public List<MasterSourceVO> getAllMasterSource() {

		return masterSourceRepo.findAllByOrderByUpdatedDateDesc().stream().map(masterSourceMapper::mapToVo).collect(Collectors.toList());
	}

	public MasterSourceVO getMasterSourceById(Integer masterId) {
		Optional<MasterSource> masterSource = masterSourceRepo.findById(masterId);
		if (masterSource.isPresent()) {
			return masterSourceMapper.mapToVo(masterSource.get());
		} else {
			return null;
		}

		/*
		 * int size = listOptional .map(List::size) .orElse(0);
		 */
	}

	public MasterSourceVO saveMasterData(MasterSourceVO masterSourceVO) {
		MasterSource masterSource = masterSourceMapper.mapToEntity(masterSourceVO);
		masterSource = masterSourceRepo.save(masterSource);
		masterSourceVO.setMasterId(masterSource.getMasterId());
		// delete if any exists -- this is to make sure it does not duplicate in the update cases
		sourceNotificationRepo.deleteByMasterSource_MasterId(masterSource.getMasterId());
		// save email
		List<SourceNotification> sourceNotifications = new ArrayList<>();
		if (masterSourceVO.getNotificationEmails() != null) {
			for (String x : masterSourceVO.getNotificationEmails()) {
				SourceNotification sn = new SourceNotification();
				sn.setEmailaddress(x);
				sn.setMasterSource(masterSource);
				sourceNotifications.add(sn);
			}
		}
		sourceNotificationRepo.saveAll(sourceNotifications);

		return masterSourceVO;
	}

	public String deleteMasterSource(Integer masterId) {
		masterSourceRepo.deleteById(masterId);
		return "Deleted";
	}

	@Transactional
	public String saveSourceBulk(MasterSourceVO masterSourceVO) {

		if (masterSourceVO.getUrl() != null && masterSourceVO.getUrl().length() > 0) {
			String[] urls = masterSourceVO.getUrl().split(",");
			MasterSource masterSource = masterSourceMapper.mapToEntity(masterSourceVO);
			// first URLs is master
			masterSource.setUrl(urls[0]);
			// save master
			masterSource = masterSourceRepo.save(masterSource);

			List<SourceAction> sourceActionsList = new ArrayList<>();
			int counter = 0;
			for (String url : urls) {
				// skip 1st url
				if (counter++ <= 0)
					continue;
				SourceAction sourceAction = new SourceAction();
				sourceAction.setMasterSource(masterSource);
				sourceAction.setUrl(url);
				sourceAction.setStepSeq(counter - 1);
				sourceActionsList.add(sourceAction);
			}
			// delete all the details of this master source
			sourceActionRepo.deleteByMasterSource_MasterId(masterSource.getMasterId());
			scrapperLogRepo.deleteByMasterSource_MasterId(masterSource.getMasterId());
			monitoringSourceRepoeRepo.deleteByMasterSource_MasterId(masterSource.getMasterId());
			// save details
			sourceActionRepo.saveAll(sourceActionsList);
			// at the end call scrapper not needed it will be done by
			// manual process 
			// mastersource_download(masterSource.getMasterId(),"PDF");
			return "Success";
		} else {
			return "No URLs to save";
		}
		
	}

	public List<MasterSourceVO> getAllMonitoringsource() {

		return masterSourceRepo.findAllByOrderByUpdatedDateDesc().stream().map(masterSourceMapper::mapWithMonitoring)
				.collect(Collectors.toList());

	}

	public String mastersource_download(Integer masterId) {

		System.out.println(masterId +" Scraping process started..." + new Date());
		//String base_dir = fileStorageProperties.getDownloadLocation() + masterId +"\\" + (new Date().getTime());
		String base_dir = fileStorageProperties.getDownloadLocation() + masterId;
		//get last url for scraping
		String url = null;
		String fileType = null;
		MonitoringSource mss = null;
		Optional<MasterSource> masterSourceOptional = masterSourceRepo.findById(masterId);
		if (masterSourceOptional.isPresent()) {
			MasterSource ms = masterSourceOptional.get();
		    fileType = ms.getType();
			List<SourceAction> lst = ms.getSourceActions();
			if(lst !=null && lst.size()>0) {
				
				List<SourceAction> lstsorted = lst.stream().sorted(Comparator.comparingInt(SourceAction::getStepSeq)).collect(Collectors.toList());
				if(lstsorted !=null && lstsorted.size()>0)
				{
					url = lstsorted.get(lstsorted.size() - 1).getUrl();
				}
				
			}else {
				url = ms.getUrl();
			}
			//save running
			mss = monitoringSourceService.saveMsStatus(masterSourceOptional.get());
		}else {
			return "";
		}
		System.out.println(masterId +" **** The URL sent to scrapper is "+url );
		Map<String, String> filesMap = ScrapingNews.download(url, fileType, base_dir);
		System.out.println(masterId +" After completing the downloads." + new Date());

		// once done loop through the directory and save all the files to DB
		try (Stream<Path> walk = Files.walk(Paths.get(base_dir))) {
			
			System.out.println(masterId +" Statring to scan folder" + new Date());

			Optional<MasterSource> masterSource = masterSourceRepo.findById(masterId);
			List<ScrapperLog> logs = new ArrayList<>();
			walk
			.filter(x->!Files.isDirectory(x))
			.filter(x-> x.getFileName().toString().indexOf("_Mac") <=0 )
			.filter(x-> x.getFileName().toString().indexOf("_original") <=0 )
			.forEach(x -> {
				ScrapperLog log = new ScrapperLog();
				if (masterSource.isPresent()) {
					log.setMasterSource(masterSource.get());
				}
				log.setDatetime(new Date());
				try {
					
					//call api to make this file grayscale
					String grayscaleApiurl = fileStorageProperties.getGrayscaleApiurl();
					System.out.println(masterId +" calling grayscaleApiurl" + grayscaleApiurl);
					boolean res = callGrayscaleApi(x.getFileName().toString(), Paths.get(base_dir), grayscaleApiurl);
					System.out.println(masterId +" done with callGrayscaleApi method " + res);
					//end of call
					
					System.out.println(masterId +" calling removeHyperlinks" + new Date());
					String fileName= FileStorageService.removeHyperlinks(x.getFileName().toString(), Paths.get(base_dir));
					System.out.println(masterId +" file name after removeHyperlinks " +fileName);
					x.toFile().delete();
					log.setFileName(fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//log.setFileName(x.getFileName().toString());
				//change path from obs to url
				// C:\\wamp64\\www\\clarivate\\upload\\ to be replaced with http://cademo.macrosoftinc.com\\clarivate\\upload\\
				String finalDir = fileStorageProperties.getDownloadHostname() + base_dir.split("www")[1];
				System.out.println(masterId +" finalDir " +finalDir);
				log.setPath(finalDir);
				// skip this record if filename is null
				if(!Util.isNull(log.getFileName()))
				{
					String desc = "";
					if(x.getFileName()!=null && filesMap!=null)
					{
						String value = filesMap.get(x.getFileName().toString());
						if(!Util.isNull(value))
						{
							desc = value;
						}
					}
					log.setDescription(desc);
					logs.add(log);
				}
			});
			if(logs!=null && logs.size()>0)
			{
				//delete all file first
				scrapperLogRepo.deleteByMasterSource_MasterId(masterId);
				//now save
				scrapperLogRepo.saveAll(logs);
				//update the rec with a success message
				mss.setDatetime(new Date());
				mss.setError("Successfully download");
				mss.setStatus("Success");
				mss.setMasterSource(masterSource.get());
				monitoringSourceRepoeRepo.save(mss);
			}else {
				//update the rec with a success message
				mss.setDatetime(new Date());
				mss.setError("download failed");
				mss.setStatus("Failed");
				mss.setMasterSource(masterSource.get());
				monitoringSourceRepoeRepo.save(mss);
			}
			System.out.println(masterId +" last bloak of code after walking the folders "+ new Date());

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(masterId +" end of thread block of code "+ new Date());
	

		return "Request Accepted";
	}

	private boolean callGrayscaleApi(String file, Path srcLocation, String apiUrl) {

		String finalFilePath = srcLocation.toString() + "\\" + file;
		if(finalFilePath!=null && finalFilePath.length()>0)
		{
			finalFilePath = finalFilePath.replace('\\', '/');
		}
		try {
			apiUrl = apiUrl + finalFilePath ;
			// open a rest client
			Client client = ClientBuilder.newClient();
			// get data in Json form
			Response res= client.target(apiUrl).request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(Boolean.class, MediaType.APPLICATION_JSON));
			System.out.println(res);
			client.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	public static void main(String[] args) {
		
		(new MasterSourceService())
		.callGrayscaleApi("1.pdf", Paths.get("C:\\wamp64\\www\\"),
				"http://192.168.1.117:8282/api/file?filepath=");
		
	}

}
