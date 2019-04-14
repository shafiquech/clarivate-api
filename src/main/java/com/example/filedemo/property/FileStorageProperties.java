package com.example.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String downloadLocation;
    private String downloadHostname;
    private String grayscaleApiurl;
    
    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

	public String getDownloadLocation() {
		return downloadLocation;
	}

	public void setDownloadLocation(String downloadLocation) {
		this.downloadLocation = downloadLocation;
	}

	public String getDownloadHostname() {
		return downloadHostname;
	}

	public void setDownloadHostname(String downloadHostname) {
		this.downloadHostname = downloadHostname;
	}

	public String getGrayscaleApiurl() {
		return grayscaleApiurl;
	}

	public void setGrayscaleApiurl(String grayscaleApiurl) {
		this.grayscaleApiurl = grayscaleApiurl;
	}
    
	
	
}
