package com.clarivate.scraping.core;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

    
    

    

	public class ScrapingNews {
	   
	   	
		 public static String PDFChecker(String url) {
		    	try {
		    	
		    	
		    		Connection.Response response = Jsoup
		        	        .connect(url)
		        	        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36") 
		    			       .header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
		    			       .header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3")
		    			       .header("Accept-Encoding" ,"none")
		    			       .header("Accept-Language", "en-US,en;q=0.9")
		    			       .header("Connection", "keep-alive")
		        	        .ignoreContentType(true)
		        	        .followRedirects(false)
		        	        .execute();    	
		    
		    	
		    	String tp = response.headers().toString();
		    	if(!tp.contains("filename")) {
		    		return "filename not found";
		    	}
		      
		    	int index_pdf = tp.indexOf(".pdf");
		    	
		    	if(index_pdf != -1) {
		    	
		    	
		    	String tw = tp.substring(0,index_pdf+4);
		    	int index_file = tw.lastIndexOf("=")+1;
		    	
		    	String filename = tp.substring(index_file, index_pdf+4);
		    	
		    	return filename;
		    	}
		    	
		    	
		    	
		    	}catch(Exception err) {
		    	err.printStackTrace();	
		    	}
		    	
		    	return "No PDF Found";
		    	
		    }
		 
		 
	   public static Document httpRequest(String url) {
		   try {
		   Document doc = Jsoup.connect(url.toString())			       
			       .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36") 
			       .header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
			       .header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			       .header("Accept-Encoding" ,"none")
			       .header("Accept-Language", "en-US,en;q=0.9")
			       .header("Connection", "keep-alive")
			       .get();
		   return doc;
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
		   return Jsoup.parse("<html></html>");
		   
	   }
	   
	   public static void PDFDownload(String url, String filePath) {
		   try {	 
				URL website = new URL(url);
				
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				
				FileOutputStream fos = new FileOutputStream(filePath);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				}catch(Exception e) {
				
					System.out.println(e);
				}
	   }
	   
	   public static class DuplicateChecker implements FilenameFilter{
		   public static String str;
		   public boolean accept(File dir, String name) {
			   if(name.contains(str)) {
				   
				   
				   return true;
			   }
			   return false;
			   
		   }
		   public DuplicateChecker(String name) {
			   str = name;
			   
		   }
		   
		   
	   }
	   
	   public static void HTMLPDFConversion(Document doc, String curr_working_dir, String docname ) {
	        try
	        {

	        String tp = doc.toString();
	        File file = new File(curr_working_dir + docname);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(tp);
            out.close();
			
			System.out.println(docname);
            String[] arr = docname.split("\\.");
            
            String pdfFile = curr_working_dir+arr[0]+".pdf";
            String htmlFile = curr_working_dir +docname.toString();
            Process p = Runtime.getRuntime().exec("cmd /C start chrome --headless --disable-gpu --print-to-pdf="+ pdfFile+ " " + htmlFile );
            BufferedReader in = new BufferedReader(
            		            new InputStreamReader(p.getInputStream()));
            String line = null;
            while((line = in.readLine())!=null) {
            System.out.println(line);
            }
            
            Thread.sleep(1000);
            Files.deleteIfExists(Paths.get(curr_working_dir+docname.toString()));
            
            
	        }
	        catch ( Exception e)
	        {
	        	System.out.println(e);
	        }
	   
	   }
	   
	   public static void NewsandEvents(String url, Document doc, String base_dir){
		   
		   
		   Elements src5_with_html = doc.select("a[href*=/Newsroom/PressAnnouncements]");
	      // System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
             src5_with_html.forEach(ele->{
            	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()-1) {
            		
            	 
            	 String[] eleurl = ele.attr("href").split("/");
            	 String docname = eleurl[eleurl.length-1];
            	 
            	 try {
            	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
            		  URL website = new URL(ele.attr("href").toString());
            		//  System.out.println(website.toString());
            		  Document doc1 = httpRequest( website.toString());
            		 
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Element header = doc1.selectFirst("meta[name=dc.description]");
		 				heading.append(header.attr("content"));
		 				Element title = doc1.selectFirst("title");
		 				Element releaseDate = doc1.selectFirst("div.release-date");
		 				Elements releaseText = doc1.selectFirst("div.release-text").select("p");
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				document.appendChild(releaseDate);
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
            	 }
            	 else {
            		 URL website = new URL(url.toString() + ele.attr("href"));
            		// System.out.println(website.toString());
    				 	
 					 Document doc1 = httpRequest(website.toString());
 					
 					Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
 					
 					Element header = doc1.selectFirst("meta[name=dc.description]");
 					heading.append(header.attr("content"));
 					Element title = doc1.selectFirst("title");
	 				Element releaseDate = doc1.selectFirst("div.release-date");
	 				Elements releaseText = doc1.selectFirst("div.release-text").select("p");
 				    Document document = new Document(title.ownText().toString());
 	
	 				document.appendChild(heading);
 		     		document.appendChild(title);
	 				document.appendChild(releaseDate);
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	 				
	
 				 
	
	 				HTMLPDFConversion(document,curr_working_directory,docname);
	 				
            		 
            	 	 
            	 }
            	 Thread.sleep(4000); 	 
            	 }catch(Exception e) {
            		e.printStackTrace(); 
            	 }
            	 
           	 
             }	 
             });		       
 			 
 	
 			
	       }
	   }
	   
	   public static void Speeches(String url, Document doc, String base_dir) {
	   Elements src5_with_html = doc.select("a[href*=/NewsEvents/Speeches]");
      // System.out.println(src5_with_html.size());
       
       String curr_working_directory = base_dir + "\\";
       File finalDir = new File(curr_working_directory);
		if (!finalDir.exists()) {
			finalDir.mkdirs();
		}
       if(src5_with_html.size()>0) {
    	 
    	   
         src5_with_html.forEach(ele->{
        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()-1) {
        		
        	 
        	 String[] eleurl = ele.attr("href").split("/");
        	 String docname = eleurl[eleurl.length-1];
        	 if(!("default.htm".contentEquals(docname)))
        	 {	 
        	 try {
        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
        		  URL website = new URL(ele.attr("href").toString());
        		//  System.out.println(website.toString());
        		  Document doc1 = httpRequest( website.toString());			       
	 				       
	 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
	 				Element article = doc1.selectFirst("article");
	 				Element header = doc1.selectFirst(".head1_body");
	 				heading.append(header.text());
	 				Element title = doc1.selectFirst("title");
	 		
	 				Elements releaseText = article.select("p");
	 				Document document = new Document(docname.toString());
	 				document.appendChild(heading);
	 				document.appendChild(title);
	 		
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	          HTMLPDFConversion(document,curr_working_directory,docname);    		  
        	 }
        	 else {
        		 String wb = url.toString().substring(0,url.lastIndexOf("/"))+"/"+eleurl[eleurl.length-1];
        		 
        		 URL website = new URL(wb);
        	
        		  
        		 
        		 
        		 
        		// System.out.println(website.toString());
				 	
					Document doc1 = httpRequest(website.toString());
					
					Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
					Element article = doc1.selectFirst("article");
					Element header = doc1.selectFirst(".head1_body");
					heading.append(header.text());
					Element title = doc1.selectFirst("title");
 			
 				Elements releaseText = article.select("p");
				    Document document = new Document(title.ownText().toString());
	
 				document.appendChild(heading);
		     		document.appendChild(title);
 			
 				for(Element ele1:releaseText) {
 					document.appendChild(ele1);
 				}
 				

				 

 				HTMLPDFConversion(document,curr_working_directory,docname);
        		 
        	 	 
        	 }
        	 Thread.sleep(4000); 	 
        	 }catch(Exception e) {
        		e.printStackTrace(); 
        	 }
        	 
        	 }
         }	 
         });		       
			 
	
			
       }
	   
   }
	   public static void WarningLetters(String url, Document doc, String base_dir) {
		   
		    
		   Elements src5_with_html = doc.selectFirst("#WarningLetter_sortid").select("tbody").select("tr");
	     //  System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()) {
	        		
	        	 
	        	 String[] eleurl = ele.select("a").attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  URL website = new URL(ele.attr("href").toString());
	        	//	  System.out.println(website.toString());
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 	
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 	
		 				Element title = doc1.selectFirst("title");
		 	
		 				Elements releaseText = article.select("table").nextAll();
		 				Document document = new Document(docname.toString());
		 	
		 				document.appendChild(title);
		 	
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	        
	        		 String[] baseurl1 = url.split("https://");
	        		 String[] baseurl2 = baseurl1[1].split("/");
	        		 String wb = "https://"+baseurl2[0].toString()+ ele.selectFirst("a").attr("href").toString();
	        		 URL website = new URL(wb);
	        		 
	        
	        		  
	        		 
	        		 
	        		 
	        	//	 System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
			
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 	
		 				Element title = doc1.selectFirst("title");
		 	
		 				Elements releaseText = article.select("table").nextAll();
		 				Document document = new Document(docname.toString());
		 	
		 				document.appendChild(title);
		 	
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 }
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }
	   
	   
	   public static void UntitledLetters(String url, Document doc, String base_dir) {
		   Elements src5_with_html = doc.select("article").select(".panel").select(".panel-default").select(".box").select("a[href$=.htm]");
	  //     System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  URL website = new URL(ele.attr("href").toString());
	        		
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 				heading.append(header.text());
		 				Element title = doc1.selectFirst("title");
		 			
		 				Elements releaseText = article.select("header").nextAll();
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 			
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	        		 String wb = url.toString().substring(0,url.lastIndexOf("/"))+"/"+eleurl[eleurl.length-1];
	        		 
	        		 URL website = new URL(wb);
	        		
	        		  
	        		 
	        		 
	        		 
	        		// System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
						Element article = doc1.selectFirst("article");
						Element header = doc1.selectFirst(".head1_body");
						heading.append(header.text());
						Element title = doc1.selectFirst("title");
	 				
	 				Elements releaseText = article.select("header").nextAll();
					    Document document = new Document(title.ownText().toString());
		
	 				document.appendChild(heading);
			     		document.appendChild(title);
	 				
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	 				

					 

	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }
	   
	   public static void MemorandaOfUnderstanding(String url, Document doc, String base_dir) {
		   Elements src5_with_html = doc.select("a[href*=/MemorandaofUnderstandingMOUs/]");
	     System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  URL website = new URL(ele.attr("href").toString());
	        	//	  System.out.println(website.toString());
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 				heading.append(header.text());
		 				Element title = doc1.selectFirst("title");
		 				
		 				Elements releaseText = article.select("header").nextAll();
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	        		 String wb = url.toString().substring(0,url.lastIndexOf("/"))+"/"+eleurl[eleurl.length-1];
	        		 
	        		 URL website = new URL(wb);
	        		 
	        		  
	        		 
	        		 
	        		 
	        	//	 System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
						Element article = doc1.selectFirst("article");
						Element header = doc1.selectFirst(".head1_body");
						heading.append(header.text());
						Element title = doc1.selectFirst("title");
	 				
	 				Elements releaseText = article.select("header").nextAll();
					    Document document = new Document(title.ownText().toString());
		
	 				document.appendChild(heading);
			     		document.appendChild(title);
	 				
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	 				

					 

	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }

	   public static void RestrictionLetters(String url, Document doc, String base_dir) {
		   
		   
		   Elements src5_with_html = doc.select("article").select("tbody").select("tr").select("a[href$=.htm]");
	       System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()) {
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  
	        		  
	        		 URL website = new URL(ele.attr("href").toString());
	        		//  System.out.println(website.toString());
	        		  
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Elements h3 = doc1.select(".head1_body");
		 				Elements allnext = h3.nextAll().select("table");
		 				Elements tab = allnext.get(0).select("h3");
	                    Elements rest = doc1.select(".head1_body ~ *").select("div");	 				
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				
		 				
		
		 				for(Element ele1:rest) {
		 					if(ele1.attr("class")==".clearfloat") {
		 						break;
		 					}
		 					else {
		 					document.appendChild(ele1);
		 					}
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	    
	        		 String[] baseurl1 = url.split("https://");
	        		 String[] baseurl2 = baseurl1[1].split("/");
	        		 String wb = "https://"+baseurl2[0].toString()+ ele.attr("href").toString();
	        		 URL website = new URL(wb);
	        		 
	    
	        		  
	        		 
	        		 
	        		 
	        	//	 System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Elements h3 = doc1.select(".head1_body");
		 				Elements allnext = h3.nextAll().select("table");
		 				Elements tab = allnext.get(0).select("h3");
		 				Elements rest = tab.nextAll();
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				for(Element ele2: rest) {
		 					document.appendChild(ele2);
		 				}
		
		 				for(Element ele1:rest) {
		 					if(ele1.attr("class")==".clearfloat") {
		 						break;
		 					}
		 					else {
		 					document.appendChild(ele1);
		 					}
		 				}
	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 }
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }

	   public static void NIDPOE_NOOH_Letters(String url, Document doc, String base_dir) {
		   Elements src5_with_html = doc.select("table").select("a:contains(Text)");
	    
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  URL website = new URL(ele.attr("href").toString());
	        	//	  System.out.println(website.toString());
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 				heading.append(header.text());
		 				Element title = doc1.selectFirst("title");
		
		 				Elements releaseText = article.select("table").nextAll();
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		
		 				for(Element ele1:releaseText) {
		 					document.appendChild(ele1);
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	        		 String wb = url.toString().substring(0,url.lastIndexOf("/"))+"/"+eleurl[eleurl.length-1];
	        		 
	        		 URL website = new URL(wb);
	   
	        		  
	        		 
	        		 
	        		 
	    
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Element article = doc1.selectFirst("article");
		 				Element header = doc1.selectFirst(".head1_body");
		 				heading.append(header.text());
		 				Element title = doc1.selectFirst("title");
		
		 				Elements releaseText = article.select("table").nextAll();
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	 				

					 

	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }
	   public static void MedWatchSafetyAlerts(String url, Document doc, String base_dir) {
	   
	   Elements src5_with_html = doc.select(".js-tweet-text-container").select("a");
	   
       System.out.println(src5_with_html.size());
       
       String curr_working_directory = base_dir + "\\";
       File finalDir = new File(curr_working_directory);
		if (!finalDir.exists()) {
			finalDir.mkdirs();
		}
       if(src5_with_html.size()>0) {
    	 
    	   
         src5_with_html.forEach(ele->{
        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()) {
        		
        	 
        	 String[] eleurl = ele.attr("href").split("/");
        	 String docname = eleurl[eleurl.length-1];
        	 if(!("default.htm".contentEquals(docname)))
        	 {	 
        	 try {
        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
        		  URL website = new URL(ele.attr("href").toString());
        	//	  System.out.println(website.toString());
        		  Document doc1 = httpRequest( website.toString());			       
	 				       
	 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
	 				Element title = doc1.selectFirst("title");
	 			
	 				Elements releaseText = doc1.select("article").select("header").nextAll();
	 				heading.append(title.text());
	 				
	 			
	 				Document document = new Document(docname.toString());
	 				document.appendChild(heading);
	 			
	 				document.appendChild(title);
	 				
	 			
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	 				
	          HTMLPDFConversion(document,curr_working_directory,docname);    		  
        	 }
        	 else {
        		 String wb = url.toString().substring(0,url.lastIndexOf("/"))+"/"+eleurl[eleurl.length-1];
        		 
        		 URL website = new URL(wb);
        		 
        		  
        		 
        		 Document doc1 = httpRequest( website.toString());
        		 
        		 Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
	 				Element title = doc1.selectFirst("title");
	 				
	 				Elements releaseText = doc1.select("article").select("header").nextAll().select(":not(#recall-photos)");
	 				heading.append(title.text());
	 				
	 				
	 				Document document = new Document(docname.toString());
	 				document.appendChild(heading);
	 				
	 				document.appendChild(title);
	 				
	 				
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
 				HTMLPDFConversion(document,curr_working_directory,docname);
        		 
        	 	 
        	 }
        	 Thread.sleep(4000); 	 
        	 }catch(Exception e) {
        		e.printStackTrace(); 
        	 }
        	 
        	 }
         }	 
         });		       
			 
	
			
       }
	   
   }
   
	   public static void EMHTML(String url, Document doc, String base_dir) {
		   
		   
		   Elements src5_with_html = doc.select("li.ecl-list-item").select("a[href^=/en/news]");
		   
	       
	       System.out.println(src5_with_html.size());
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()) {
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  
	        		  
	        		 URL website = new URL(ele.attr("href").toString());
	        	//	  System.out.println(website.toString());
	        		  
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Elements h3 = doc1.select(".head1_body");
		 				Elements allnext = h3.nextAll().select("table");
		 				Elements tab = allnext.get(0).select("h3");
	                    Elements rest = doc1.select(".head1_body ~ *").select("div");	 				
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				
		 				
		
		 				for(Element ele1:rest) {
		 					if(ele1.attr("class")==".clearfloat") {
		 						break;
		 					}
		 					else {
		 					document.appendChild(ele1);
		 					}
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname+".html");    		  
	        	 }
	        	 else {
	       		 
	        		 String first_string = ele.attr("href").split("/")[1];
	        		 int first_index = url.indexOf("/"+first_string);
	        		 String website = url.substring(0,first_index) + ele.attr("href");
	        		  
	        		 
	        		 
	        		 
	        	//	 System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
						
		 				Elements h3 = doc1.select("h1.ecl-heading");
		 				if(h3.size()>0) {
		 					heading.append(h3.get(0).text());
		 				}
		 				
		 				Elements release_date = doc1.select(".group-ema-news-date");
		 				Elements allnext = doc1.select(".ecl-editor").select(".first :not(p img[alt*= icon])");
		
		 				
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				for(Element ele44: allnext) {
		 				
		 					document.appendChild(ele44);
		 					
		 				}
		 				
	 				HTMLPDFConversion(document,curr_working_directory,docname+".html");
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 }
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }   
   public static void ELatestNews(String url, Document doc, String base_dir) {
		   
		   
		   Elements src5_with_html = doc.select("div.view-content").select("tbody").select("tr").select("a[^href$=.pdf]");
	       System.out.println(src5_with_html.size());
	       
	       String curr_working_directory = base_dir + "\\";
	       File finalDir = new File(curr_working_directory);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}
	       if(src5_with_html.size()>0) {
	    	 
	    	   
	         src5_with_html.forEach(ele->{
	        	 if(src5_with_html.indexOf(ele)!=src5_with_html.size()) {
	        		
	        	 
	        	 String[] eleurl = ele.attr("href").split("/");
	        	 String docname = eleurl[eleurl.length-1];
	        	 if(!("default.htm".contentEquals(docname)))
	        	 {	 
	        	 try {
	        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
	        		  
	        		  
	        		 URL website = new URL(ele.attr("href").toString());
	        	//	  System.out.println(website.toString());
	        		  
	        		  Document doc1 = httpRequest( website.toString());			       
		 				       
		 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Elements h3 = doc1.select(".head1_body");
		 				Elements allnext = h3.nextAll().select("table");
		 				Elements tab = allnext.get(0).select("h3");
	                    Elements rest = doc1.select(".head1_body ~ *").select("div");	 				
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				
		 				
		
		 				for(Element ele1:rest) {
		 					if(ele1.attr("class")==".clearfloat") {
		 						break;
		 					}
		 					else {
		 					document.appendChild(ele1);
		 					}
		 				}
		          HTMLPDFConversion(document,curr_working_directory,docname);    		  
	        	 }
	        	 else {
	    
	        		 String[] baseurl1 = url.split("https://");
	        		 String[] baseurl2 = baseurl1[1].split("/");
	        		 String wb = "https://"+baseurl2[0].toString()+ ele.attr("href").toString();
	        		 URL website = new URL(wb);
	        		 
	    
	        		  
	        		 
	        		 
	        		 
	        	//	 System.out.println(website.toString());
					 	
						Document doc1 = httpRequest(website.toString());
						
						Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
		 				Elements h3 = doc1.select(".head1_body");
		 				Elements allnext = h3.nextAll().select("table");
		 				Elements tab = allnext.get(0).select("h3");
		 				Elements rest = tab.nextAll();
		 				Element title = doc1.selectFirst("title");
		 				heading.append(h3.text());
		 				
		 				
		 				
		 				
		 				
		 				Document document = new Document(docname.toString());
		 				document.appendChild(heading);
		 				document.appendChild(title);
		 				for(Element ele2: rest) {
		 					document.appendChild(ele2);
		 				}
		
		 				for(Element ele1:rest) {
		 					if(ele1.attr("class")==".clearfloat") {
		 						break;
		 					}
		 					else {
		 					document.appendChild(ele1);
		 					}
		 				}
	 				HTMLPDFConversion(document,curr_working_directory,docname);
	        		 
	        	 	 
	        	 }
	        	 Thread.sleep(4000); 	 
	        	 }catch(Exception e) {
	        		e.printStackTrace(); 
	        	 }
	        	 
	        	 }
	         }	 
	         });		       
				 
		
				
	       }
		   
	   }
   //Special case 2
      
   
   public static void WhatsNew(String url, Document doc, String base_dir) {
	   Elements src5_with_html = doc.select("div.tx-usrwhatsnew-pi1").select("p").select("a");
       
       
	   int flag = 0;
       String curr_working_directory = base_dir +"\\";
       File finalDir = new File(curr_working_directory);
		if (!finalDir.exists()) {
			finalDir.mkdirs();
		}
       if(src5_with_html.size()>0) {
    	 
    	   
         for(Element ele:src5_with_html) {
        	 
        		
        	 
        	 String[] eleurl = ele.attr("href").split("/");
        	 String docname = eleurl[eleurl.length-1];
        	 if(!("default.htm".contentEquals(docname)))
        	 {	 
        	 try {
        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
        		  URL website = new URL(ele.attr("href").toString());
        		
        		  Document doc1 = httpRequest( website.toString());			       
	 				       
	 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
	 				Element article = doc1.selectFirst("article");
	 				Element header = doc1.selectFirst(".head1_body");
	 				heading.append(header.text());
	 				Element title = doc1.selectFirst("title");
	 			
	 				Elements releaseText = article.select("header").nextAll();
	 				Document document = new Document(docname.toString());
	 				document.appendChild(heading);
	 				document.appendChild(title);
	 			
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	          HTMLPDFConversion(document,curr_working_directory,docname);    		  
        	 }
        	 else {
        		 String wb = url.toString()+"/"+ele.attr("href").toString();
        		 String[] title_array = ele.attr("href").toString().split("#");
        		 String title = title_array[1] + ".html";
        		 URL website = new URL(wb);
        		
        		  
        		 
        		 
                 if(wb.contains("whatsnew") ) {
                	 if(flag==1) {
                		 continue;
                	 }
                 }
        		 
        		 
        	//	 System.out.println(website.toString());
				 	
					Document doc1 = httpRequest(website.toString());
					
					 				
 				Elements releaseText = doc1.select("div.content");
				    Document document = new Document(title.toString());
	
                 				
 				for(Element ele1:releaseText) {
 					document.appendChild(ele1);
 				}
 				

				 

 				HTMLPDFConversion(document,curr_working_directory,title);
        		 
                 
                 if(wb.contains("whatsnew")) {
                	 flag =1 ;
                 }
        	 }
        	 Thread.sleep(4000); 	 
        	 }catch(Exception e) {
        		e.printStackTrace(); 
        	 }
        	 
        	 
         }	 
         }	       
			 
	
			
       }
	   
   }
   
   public static void FDHTML(String url, Document doc, String base_dir) {
	   Elements src5_with_html = doc.select("table.listtable").select("a");
       
       
	   int flag = 0;
       String curr_working_directory = base_dir + "\\";
       File finalDir = new File(curr_working_directory);
		if (!finalDir.exists()) {
			finalDir.mkdirs();
		}
       if(src5_with_html.size()>0) {
    	 
    	   
         for(Element ele:src5_with_html) {
        	 
        		
        	 
        	 String[] eleurl = ele.attr("href").split("/");
        	 String docname = eleurl[eleurl.length-1];
        	 if(!("default.htm".contentEquals(docname)))
        	 {	 
        	 try {
        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
        		  URL website = new URL(ele.attr("href").toString());
        		
        		  Document doc1 = httpRequest( website.toString());			       
	 				       
	 				Element heading = Jsoup.parse(new String("<h1></h1>")).selectFirst("h1");
	 				Element article = doc1.selectFirst("article");
	 				Element header = doc1.selectFirst(".head1_body");
	 				heading.append(header.text());
	 				Element title = doc1.selectFirst("title");
	 			
	 				Elements releaseText = article.select("header").nextAll();
	 				Document document = new Document(docname.toString());
	 				document.appendChild(heading);
	 				document.appendChild(title);
	 			
	 				for(Element ele1:releaseText) {
	 					document.appendChild(ele1);
	 				}
	          HTMLPDFConversion(document,curr_working_directory,docname);    		  
        	 }
        	 else {
        		 int index = url.toString().lastIndexOf("/");
        		 String wbt = url.substring(0, index+1);
        		 
        		 String wb = wbt+ele.attr("href").toString();
        		 
        		 String[] title_index = ele.attr("href").toString().split("=");
        		 String title = title_index[title_index.length-1];
        		 URL website = new URL(wb);
        		
        		  
        		 
        		 
                 
        		 
        		 
        	//	 System.out.println(website.toString());
				 	
					Document doc1 = httpRequest(website.toString());
					Elements h3 = doc1.select("h3");
					Element heading =h3.get(h3.size()-1);
					Elements div_ele = doc1.select("div#ContentPlaceHolder1_PageContentUC_TitlePanel");
					Elements after = div_ele.nextAll();
					Elements text = after.select(".edit");
 				Elements releaseText = doc1.select("body"); 
				    
				Document document = new Document(title.toString());    
 				Element html_element = document.selectFirst("html");
				    
				Element more_file = document.selectFirst(".moreFileTT");    
	
                 document.appendChild(heading);				
 				
 				for(Element ele1:text) {
 					document.appendChild(ele1);
 				}
 				if(more_file !=null) {
                  document.appendChild(more_file); 					
 				}
 				

				 

 			HTMLPDFConversion(document,curr_working_directory,title+".html");
        		 
                 
                 
        	 }
        	 Thread.sleep(4000); 	 
        	 }catch(Exception e) {
        		e.printStackTrace(); 
        	 }
        	 
        	 
         }	 
         }	       
			 
	
			
       }
	   
   }
   public static void FDGeneral(String url, Document doc, String base_dir) {
	   Elements src5_with_html = doc.select("table a").not("img");
       
       
	   int flag = 0;
       String curr_working_directory = base_dir +"\\";
       File finalDir = new File(curr_working_directory);
		if (!finalDir.exists()) {
			finalDir.mkdirs();
		}
       if(src5_with_html.size()>0) {
    	 
    	   
         for(Element ele:src5_with_html) {
        	 
        	
        	 if(ele.attr("href").contains("GetFile")) {
        		 continue;
        	 }
        	 
        	 String[] eleurl = ele.attr("href").split("/");
        	 String docname = eleurl[eleurl.length-1];
        	 if(!("default.htm".contentEquals(docname)))
        	 {	 
        		 String title="PDF_Doc";
        		 Document doc1;
        	 try {
        	 if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
        		  URL website = new URL(ele.attr("href").toString());
        		
        		  doc1 = httpRequest( website.toString());
        		  //int title_index = ele.attr("href").toString().lastIndexOf("=");
        		  //title = ele.attr("href").substring(title_index, ele.attr("href").toString().length());
	 				           		  
        	 }
        	 else {
        		 int index = url.toString().lastIndexOf("/");
        		 String wbt = url.substring(0, index+1);
        		 
        		 String wb = wbt+ele.attr("href").toString();
        		 
        		 String[] title_index = ele.attr("href").toString().split("=");
        		 
        		 if(ele.attr("href").toString().charAt(0)=='/'){
        		 String first_string = ele.attr("href").split("/")[1];
        		 int first_index = url.indexOf("/"+first_string);
        		 if(first_index==-1) {
        		 if(wb.charAt(wb.length()-1)=='/') {
        			 wb = wb + ele.attr("href").substring(1,ele.attr("href").length());
        		 }
        		 else {
        			 wb = wb + ele.attr("href");
        		 }
        		 }else {
        			 wb = wb.substring(0, first_index) + ele.attr("href");
        		 }
        		 }else {
        			 wb = url.substring(0,index)+"/"+ele.attr("href");
        		 }
        		 
        		 //title = title_index[title_index.length-1];
        		 URL website = new URL(wb);
        		
        		  
        		 
        		 
                 
        		 
        		 
        	//	 System.out.println(website.toString());
				 	
					 doc1 = httpRequest(website.toString());
        	 }
					Elements h3 = doc1.select("h3");
					Element heading =h3.get(h3.size()-1);
					
 				    Elements text = doc1.select(".edit");
					Elements releaseText = doc1.select("body"); 
				    //Document document = Jsoup.parse("<html></html>");
				Document document = new Document(title.toString());    
 				Element html_element = document.selectFirst("html");
				//document.appendChild(releaseText);    
				Element more_file = document.selectFirst(".moreFileTT");    
	
                 document.appendChild(heading);				
 				/*for(Element ele1:releaseText) {
 					document.appendChild(ele1);
 				}*/
 				for(Element ele1:text) {
 					document.appendChild(ele1);
 				}
 				if(more_file !=null) {
                  document.appendChild(more_file); 					
 				}
 				

				 
 				 DuplicateChecker t = new DuplicateChecker(title);
				 File ft = new File(curr_working_directory);
				 String[] ft2 = ft.list(t);
				 String filetitle = title+"_"+ft2.length;
 			     HTMLPDFConversion(document,curr_working_directory,filetitle+".html");
        		 
                 
                 
        	 
        	 Thread.sleep(4000); 	 
        	 }catch(Exception e) {
        		e.printStackTrace(); 
        	 }
        	 
        	 
         }	 
         }	       
			 
	
			
       }
	   
   }
   
   
	   @SuppressWarnings("deprecation")
	public static Map<String, String> download(String url, String fileType, String bs_dir) {
		   Map<String, String> m = new HashMap<>();
		   String MapFileName="";
		   String MapDescription = "";
		   try {
			 
			    
		       String base_dir = bs_dir;
		       //create dir if not there in the file system
				File finalDir = new File(base_dir);
				if (!finalDir.exists()) {
					finalDir.mkdirs();
				}
				
			   Document doc = httpRequest(url.toString());
		            
		   
		   
		   if("PDF".equalsIgnoreCase(fileType)) {
               Elements href_on_web = doc.select("table").select("a");		   
			   if(url.contains("/Guidances/")) {
				   Elements scripts = doc.getElementsByTag("script");
				   int pdf_first_index = scripts.toString().indexOf("<a href");
				   int pdf_last_index = scripts.toString().lastIndexOf("<a href");
				   int pdf_index = scripts.toString().substring(pdf_last_index,scripts.toString().length()-1).indexOf('>');
				   String hrefs = scripts.toString().substring(pdf_first_index, pdf_last_index+pdf_index);
				   System.out.println("abcd");
				   System.out.println(hrefs);
				   Document doc33 = Jsoup.parse(hrefs);
				   Elements src33 = doc33.select("a");
				   for (Element ele2: src33) {
					   if(!ele2.attr("href").toString().contains(".htm")) {
						   String[] resourceurl1 = ele2.attr("href").split("/");
						    	
							String filePath = base_dir+"\\"+resourceurl1[resourceurl1.length-1].toString()+".pdf";
							File file = new File(filePath);
							String[] wb = url.split("https://");
							String[] wbt = wb[1].split("/");
							String website = "https://" + wbt[0] + ele2.attr("href");
							PDFDownload(website,filePath);
							try {
							File tmpFile = new File(filePath);
							if(tmpFile.exists()) {
							MapFileName = resourceurl1[resourceurl1.length-1].toString()+".pdf";
							MapDescription = ele2.text();
							m.put(MapFileName, MapDescription);
							}
							}catch(Exception e) {
								e.printStackTrace();
							}
					   }
					   
				   }
				   return m;
			   }
			  
			   //tw
			   if(url.contains("tw/Manager")){
			   		Elements doc33 = doc.select("a:contains(.pdf)");
                    int lastindex = url.lastIndexOf("/");
                    String wt = url.substring(0,lastindex);
                    
                    for (Element ele33: doc33) {
                    String website = wt + "/"+ele33.attr("href").toString();
                    int filenameindex = website.lastIndexOf("=");
                    String filename = website.substring(filenameindex, website.length()-1).replace("=", "");
                    String filepath = base_dir+"\\"+filename+".pdf";
                    PDFDownload(website,filepath);
                    
					try {
                    File tmpFile = new File(filepath);
					if(tmpFile.exists()) {
					MapFileName = filename+".pdf";
					MapDescription = ele33.text();
					m.put(MapFileName, MapDescription);
					}
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					
                    }
                    return m;
			   
			   }
			   if(url.contains("tw/egFront")){
			   	   Elements doc33 = doc.select("a:contains(.pdf)");
                   int lastindex = url.lastIndexOf("egFront");
                   String wt = url.substring(0,lastindex);
                   
                   for (Element ele33: doc33) {
                   String website = wt + ele33.attr("href").toString();
                   int filenameindex = website.lastIndexOf("/");
                   String filename = website.substring(filenameindex, website.length()-1);
                   if(!filename.contains(".pdf")) {
                	   filename = filename + ".pdf";
                   }
                   //String filename = website.substring(filenameindex, website.length()-1).replace("=", "");
                   String filepath = base_dir+"\\"+filename;
                   PDFDownload(website,filepath);
                   
                    try {                   
					File tmpFile = new File(filepath);
					
					if(tmpFile.exists()) {
					MapFileName = filename+".pdf";
					MapDescription = ele33.text();
					m.put(MapFileName, MapDescription);
					}
                    }catch(Exception e) {
                    	e.printStackTrace();
                    }
                   }
                   return m;
			   
			   }
			   //For Pediatric links
			   if(url.contains("/DevelopmentApprovalProcess/")) {
				   Elements table = doc.selectFirst("table").select("tr");
				   
				   
				   
				   for( Element ele55: table) {
					   if(table.indexOf(ele55)>0) {
						   Elements pdflinks = ele55.select("a[href$=.pdf]");
						   
						   
						   String mergeFilename = "";
						   for(Element ele555:pdflinks) {
							   String[] resourceurl = ele555.attr("href").toString().split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();
							    mergeFilename += filename.replace(".pdf", "")+"_";
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString(); 
								
								String[] wb = url.split("https://");
								String[] wbt = wb[1].split("/");
								String website = "https://" + wbt[0] + ele555.attr("href");
								PDFDownload(website,filePath);
								try {
								File tmpFile = new File(filePath);
								if(tmpFile.exists()) {
									MapDescription+=" " +ele555.text();
								}
								}catch(Exception e) {
									e.printStackTrace();
								}
							    try {
									Thread.sleep(4000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
								
						   }
						   
						   
							PDFMergerUtility ut = new PDFMergerUtility();
							pdflinks.forEach(ele1->{
								String[] resourceurl = ele1.attr("href").split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();	
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString();
								File file = new File(filePath);
								try {
									
									if(file.exists()) {
										ut.addSource(filePath);
									}
									}catch(Exception err) {
										err.printStackTrace();
									}
								
							});
							String[] resourceurl = ele55.select("a[href$=.pdf]").get(0).attr("href").split("/");
						    
							
							ut.setDestinationFileName(base_dir+"\\"+mergeFilename.substring(0, mergeFilename.length()-1)+".pdf");
							
							
							try {
							File tmpFile = new File(base_dir+"\\"+mergeFilename.substring(0, mergeFilename.length()-1)+".pdf");
							if(tmpFile.exists()) {
							MapFileName = mergeFilename.substring(0, mergeFilename.length()-1)+".pdf";
							
							
							
							}
							}catch(Exception e) {
								e.printStackTrace();
							}
						    try {
								ut.mergeDocuments();
								m.put(MapFileName, MapDescription);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   //delete
						   pdflinks.forEach(ele1->{
								String[] resourceurl1 = ele1.attr("href").split("/");
							    //String filename1 = resourceurl[resourceurl.length-1].toString();	
								String filePath = base_dir+"\\"+resourceurl1[resourceurl1.length-1].toString();
								File file = new File(filePath);
								try {
									
									if(file.exists()) {
										file.delete();
									}
									}catch(Exception err) {
										err.printStackTrace();
									}
								
							});
						   
						   
						   
					   }
					   
				   }
						
						
					return m;
			   }
			   
			   
			   //special case 2
			   
			   if(url.contains("/health/documents/")) {
				   
				   
				   Elements table = doc.select("div.mainText");
				   Elements list_items = table.select("h3 + ul");
				   Elements firstlinks = table.select("p a[href$=.pdf]");
				   if(firstlinks.size()>0) {
					  Element firstlink = firstlinks.get(0);
					  String[] resourceurl = firstlink.attr("href").toString().split("/");
					    String filename = resourceurl[resourceurl.length-1].toString();
					    
						String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString(); 
						
						
						String website = firstlink.attr("href");
						PDFDownload(website,filePath);
						
						try {
						File tmpFile = new File(filePath);
						if(tmpFile.exists()) {
						MapFileName = filename;
						MapDescription = firstlink.text();
						m.put(MapFileName, MapDescription);
						}
						}catch(Exception e) {
							e.printStackTrace();
						}
						
					    try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				   }
			   
				   
				   
				   MapFileName = "";
				   MapDescription = "";
				   
				   for(Element ele55:list_items){
					   
						   Elements pdflinks = ele55.select("li").select("a[href$=.pdf]");
						   int ctr33 = 0;
						   MapDescription = "";
						   String mergeFilename = "";
						   for(Element ele555:pdflinks) {
							   String[] resourceurl = ele555.attr("href").toString().split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();
							    mergeFilename += filename.replace(".pdf", "")+"_";
							    if(mergeFilename.length()>100) {
							    mergeFilename = mergeFilename.substring(0,100+mergeFilename.length()%100);
							    }
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString(); 
								
								
								String website = ele555.attr("href");
								try {
								PDFDownload(website,filePath);
								try {
								File tmpFile = new File(filePath);
								if(tmpFile.exists()) {
								
								MapDescription += ele555.text();
								
								}}catch(Exception e) {
									e.printStackTrace();
								}
								ctr33=ctr33 + 1;
								}catch(Exception e) {
									e.printStackTrace();
								}
								try {
									Thread.sleep(4000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
								
						   }
						   
			                MapFileName="";			    
							PDFMergerUtility ut = new PDFMergerUtility();
							pdflinks.forEach(ele1->{
								String[] resourceurl = ele1.attr("href").split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();	
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString();
								File file = new File(filePath);
								try {
									
									if(file.exists()) {
										ut.addSource(filePath);
									}
									}catch(Exception err) {
										err.printStackTrace();
									}
								
							});
							String[] resourceurl = ele55.select("a[href$=.pdf]").get(0).attr("href").split("/");
						    
							
							ut.setDestinationFileName(base_dir+"\\"+mergeFilename.substring(0, mergeFilename.length()-1)+".pdf");
						    try {
								ut.mergeDocuments();
								try {
								File tmpFile = new File(base_dir+"\\"+mergeFilename.substring(0, mergeFilename.length()-1)+".pdf");
								if(tmpFile.exists()) {
								MapFileName = mergeFilename.substring(0, mergeFilename.length()-1)+".pdf";
								
								m.put(MapFileName, MapDescription);
								}
								}catch(Exception e) {
									e.printStackTrace();
								}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   //delete
						   
						   if(ctr33>1) { 
						   pdflinks.forEach(ele1->{
								String[] resourceurl1 = ele1.attr("href").split("/");
								
								String filePath = base_dir+"\\"+resourceurl1[resourceurl1.length-1].toString();
								File file = new File(filePath);
								try {
									
									if(file.exists()) {
										file.delete();
									}
									}catch(Exception err) {
										err.printStackTrace();
									}
								
							});
						   }
						   
						   
						   
					   
					   
				   }
						
						
				return m;	
			   }
			   //New TW code
			   if(url.contains(".tw/TC")) {
				   
				   
				   
				   Elements ele77 = doc.select("a[href*=/GetFile.ashx]");
				   String wb = url.substring(0, url.indexOf("/TC/"));
				   for(Element ele33: ele77) {
					String wt = ele33.attr("href").toString();
					String website = wb + wt;
					String filename = "pdfDoc";
					String tp = PDFChecker(website.toString());
					if(tp!="No PDF Found"){
						
						 DuplicateChecker t = new DuplicateChecker(filename);
						 File ft = new File(base_dir);
						 String[] ft2 = ft.list(t);
						 String filePath = base_dir+"//" + filename+"_"+ft2.length+".pdf";
						 PDFDownload(website, filePath);
						 try {
						    File tmpFile = new File(filePath);
							if(tmpFile.exists()) {
							MapFileName = filename+"_"+ft2.length+".pdf";
							MapDescription = ele33.text();
							m.put(MapFileName, MapDescription);
							}
						 }catch(Exception e) {
							 e.printStackTrace();
						 }
						 
					}
					
					
					
				   }
				   
				   return m;
				   
				   
				   
				   
				   
			   }
			   if(url.contains("doc_data_display") || url.contains("TC/siteList")) {
				   String[] wb = {};
				   
				   				   
				   if(url.startsWith("https://")){
					   wb = url.split("https://");
				   }
				   else {
					   wb = url.split("http://");
				   }
				   String[] wbt = wb[1].split("/");
				   
				   Elements ele771 = doc.select("div#tree1");
				   Elements ele77 = null; 
				   if(url.contains("doc_data_display")) {		   
				   ele77=doc.select("a");	
				   }else {
					   if(url.contains("TC/siteList")) {
						   //case 1
						    ele77 = doc.select("img[alt*=.pdf]");
						   //ele77 = doc.select("img[alt* = .pdf], a[title*=pdf], a[title*=PDF], a[title*=Pdf], a[href$=.pdf]");
						   
						   
					   }
				   }
					
					
				    if(ele77.size()>0){
				    	
				    	for(Element ele777: ele77) {
				    	 int i;
				    	 
				    	 
				    	 String webst = ele777.attr("href");
				    	  
						 for (i =0;i<webst.length()-1;i++) {
							 if(webst.charAt(i)=='/') {
								 if(webst.charAt(i+1)!='.') {
									 break;
								 }
							 }
						 }
						 if(url.contains("doc_data_display")) {
						 String website = "http://" + wbt[0].toString() + webst.substring(i,webst.length());
					     String tp = PDFChecker(website.toString());
					     if(tp!="No PDF Found") {
					    	 PDFDownload(website,base_dir+"\\Test"+ele77.indexOf(ele777)+".pdf");
					    	 try {
					    	 File tmpFile = new File(base_dir+"\\Test"+ele77.indexOf(ele777)+".pdf");
								if(tmpFile.exists()) {
								MapFileName = "Test"+ele77.indexOf(ele777)+".pdf";
								MapDescription = ele777.text();
								m.put(MapFileName, MapDescription);
								}
					    	 }catch(Exception e) {
					    		 e.printStackTrace();
					    	 }
					     }
						 }else {
							 Element parent = ele777.parent().selectFirst("a");
							 webst = parent.attr("href").toString();
							 
							 String website = "";
							 if(url.contains("https://")) {
							 website = "https://" + wbt[0].toString() +  webst.toString();
							 }
							 else {
								 website = "http://" + wbt[0].toString() +  webst.toString();
							 }
						     String tp = PDFChecker(website.toString());
						     try {
						     if(tp!="No PDF Found") {
						    	 if(tp!="No filename found") {
						    	 PDFDownload(website,base_dir+"\\"+"PDFdoc"+ele77.indexOf(ele777)+".pdf");
						    	 try {
						    	 File tmpFile = new File(base_dir+"\\"+"PDFdoc"+ele77.indexOf(ele777)+".pdf");
									if(tmpFile.exists()) {
									MapFileName = "PDFdoc"+ele77.indexOf(ele777)+".pdf";
									MapDescription = ele777.text();
									m.put(MapFileName, MapDescription);
									}
						    	 }catch(Exception e) {
						    		 e.printStackTrace();
						    	 }
						    	 }else {
						    		 PDFDownload(website,base_dir+"\\"+"PDFdoc"+ele77.indexOf(ele777)+".pdf");
						    		 try {
						    		 File tmpFile = new File(base_dir+"\\"+"PDFdoc"+ele77.indexOf(ele777)+".pdf");
										if(tmpFile.exists()) {
										MapFileName = "PDFdoc"+ele77.indexOf(ele777)+".pdf";
										MapDescription = ele777.text();
										m.put(MapFileName, MapDescription);
										}
						    		 }catch(Exception e) {
						    			 e.printStackTrace();
						    		 }
						    	 }
						    	 
						     }else {
						    	 System.out.println(website + "does not contain pdf");
						     }
						     }catch(Exception e) {
						    	 System.out.println(website + "does not contain pdf");
						     }
						     
						 }
					
				    }
			   }
				    return m;
			   }
			   
			   
			   
			   else {
			   
			   
			   
			   Elements src5_with_labels;
			   
			   
			   src5_with_labels = doc.select("a[href$=.pdf],a[href*=PDF], a[href*=pdf]");
			   System.out.println(src5_with_labels.attr("href"));
			   
			   System.out.println("abcd");
			   if(url.contains("consilium")) {
				   Elements src5_with_lt=src5_with_labels.select("a[href$=en/pdf]");
				   src5_with_labels = src5_with_lt;
			   }
			   else {
			   if(src5_with_labels.hasAttr("hreflang")) {
				   Elements src5_with_lt = src5_with_labels.select("a[hreflang=en]");
				   src5_with_labels =src5_with_lt;
			   }
			   }
			   
			   String baseurl = url.toString().split("/")[0] + "//"+url.toString().split("/")[2];
			   System.out.println(src5_with_labels.size());
				
	            if(src5_with_labels.size()>0) {
	            		
	            	for( Element ele: src5_with_labels)  {
				        
	            		
					String[] resourceurl = ele.attr("href").split("/");
				
						
					if(ele.attr("href").contains("https://") || ele.attr("href").contains("http://") ) {
						try {
							
						
						String website = ele.attr("href").toString();
						String fileName = new String(base_dir + "\\"+resourceurl[resourceurl.length-1].toString());
						if(!ele.attr("href").toString().contains(".pdf")) {
						
						 
						 
						 DuplicateChecker t = new DuplicateChecker(resourceurl[resourceurl.length-1]);
						 File ft = new File(base_dir);
						 String[] ft2 = ft.list(t);
						
						if(ft2.length>0) {
							fileName = new String(base_dir + "\\" + resourceurl[resourceurl.length-1].toString()+"_"+ft2.length+".pdf");
						    try {
							MapFileName = resourceurl[resourceurl.length-1].toString()+"_"+ft2.length+".pdf";
						    }catch(Exception e) {
						    	e.printStackTrace();
						    }
						}//else added 
						else {
							fileName = new String(base_dir + "\\" + resourceurl[resourceurl.length-1].toString()+".pdf");
						    try {
							MapFileName = resourceurl[resourceurl.length-1].toString()+".pdf";
						    }catch(Exception e) {
						    	e.printStackTrace();
						    }
						}
						
						}
						
						
						PDFDownload(website.replaceAll(" ", "%20"), fileName);
						
						try {
						File tmpFile = new File(fileName);
						/* New Code */
						if(fileName.contains("\\")){
							MapFileName = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.length());
						}
						if(tmpFile.exists()) {
							MapDescription = ele.text(); 
							m.put(MapFileName, MapDescription);
						}
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						}catch(Exception e) {
						
							System.out.println(e);
						}
					}
					else {
						try {
							
							
							 
							 String webst = ele.attr("href").toString();
							 int i;
							 for (i =0;i<webst.length()-1;i++) {
								 if(webst.charAt(i)=='/') {
									 if(webst.charAt(i+1)!='.') {
										 break;
									 }
								 }
							 }
							 String webst2 = webst.substring(i, webst.length());
							 String website = new String(baseurl.toString() + webst2.toString());
				           
							 if(i>0) {
								 int index = src5_with_labels.indexOf(ele);
								 /*New Change*/
								 int ft_index = url.toString().lastIndexOf("/");
				        		 String wbt = url.substring(0, ft_index+1);
				        		 
				        		 String wb = wbt;
				        		 
				   
								 String first_string = webst2.split("/")[1];
				        		 int first_index = url.indexOf("/"+first_string);
				        		 if(first_index==-1) {
				        		 if(wb.charAt(wb.length()-1)=='/') {
				        			 if(webst.charAt(0)=='/') {
				        			 wb = wbt + ele.attr("href").substring(1,ele.attr("href").length());
				        			 }
				        			 else {
				        				 wb = wbt + ele.attr("href").substring(0,ele.attr("href").length());
				        			 }
				        				 
				        		 }
				        		 else {
				        			 wb = wbt + ele.attr("href");
				        		 }
				        		 }else {
				        			 if(ele.attr("href").contains("./")) {
				        				wb = wb.substring(0,first_index) + ele.attr("href").substring(ele.attr("href").lastIndexOf("./")+1, ele.attr("href").length()); 
				        			 }
				        			 else {
				        			 wb = wb.substring(0, first_index) + ele.attr("href");
				        			 }
				        		 }
				        		 website = wb;
				        		 String checkString = "PDFdoc";
								 /*End of new change*/
				        		 
								 DuplicateChecker t;
								 if(resourceurl[resourceurl.length-1].length()>0 && resourceurl[resourceurl.length-1].contains(".pdf")) {
									 String[] tqw = resourceurl[resourceurl.length-1].split(".pdf");
									 t = new DuplicateChecker(tqw[0]);
									 checkString = tqw[0];
								 }
								 else { //if else added, remove if else and keep only whatever under else
								 t = new DuplicateChecker("PDFdoc");
								 }
								 File ft = new File(base_dir);
								 String[] ft2 = ft.list(t);
								 String fileName = "";
								 if(checkString=="PDFdoc") {
								 fileName = base_dir + "\\"+checkString+"_"+ft2.length+".pdf";
								 }else {
									 if(ft2.length==0) {
										 fileName = base_dir + "\\"+ resourceurl[resourceurl.length-1].split(".pdf")[0]+".pdf";
										 MapFileName = resourceurl[resourceurl.length-1].split(".pdf")[0]+".pdf";
									 }
									 else {
										 fileName = base_dir + "\\"+ resourceurl[resourceurl.length-1].split(".pdf")[0]+"_"+ft2.length+".pdf";
									     MapFileName = resourceurl[resourceurl.length-1].split(".pdf")[0]+"_"+ft2.length+".pdf";
									 }
								 }
								 PDFDownload(website.replaceAll(" ", "%20"), fileName);
							     try {
								 File tmpFile = new File(fileName);
							     if(tmpFile.exists()) {
							    	 MapDescription = ele.text();
							    	 
							    	 MapFileName = fileName.substring(fileName.lastIndexOf("\\")+1,fileName.length());
							    	 m.put(MapFileName, MapDescription);
							     }
							     }catch(Exception e) {
							    	 e.printStackTrace();
							     }
								}
							 else {
								String fileName = base_dir + "\\"+resourceurl[resourceurl.length-1].toString();
								PDFDownload(website.replaceAll(" ","%20"), fileName);
								try {
								File tmpFile = new File(fileName);
							     if(tmpFile.exists()) {
							    	 MapFileName = resourceurl[resourceurl.length-1].toString();
							    	 MapDescription = ele.text();
							    	 m.put(MapFileName, MapDescription);
							     }
								}catch(Exception e) {
									e.printStackTrace();
								}
							 }
							 
								
						}catch(Exception e) {
							
							System.out.println(e);
						}
					}
					//sleep for 5 seconds
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//
	            	}
	            }
			   
						         
					if(url.contains("/ApprovedProducts/")) {
						PDDocument[] pdfDocuments = {};
						MapDescription = "";
						try {
							PDFMergerUtility ut = new PDFMergerUtility();
							String mergedFilename = ""; 
							
							for(Element ele: src5_with_labels) {
								String[] resourceurl = ele.attr("href").split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();
							    mergedFilename += filename.replace(".pdf", "")+"_";
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString();
								File file = new File(filePath);
								
							
								ut.setDestinationFileName(base_dir+"\\"+mergedFilename.substring(0, mergedFilename.length()-1)+".pdf");
								try {
									
								if(file.exists()) {
									ut.addSource(filePath);
									
									try {
									MapDescription +=" "+ ele.text();
									m.remove(resourceurl[resourceurl.length-1].toString());
									}catch(Exception e) {
										e.printStackTrace();
									}
									
								}
								}catch(Exception err) {
									err.printStackTrace();
								}
							}
							String[] resourceurl = src5_with_labels.get(0).attr("href").split("/");
						   
						    
							ut.mergeDocuments();
							try {
							File tmpFile = new File(base_dir+"\\"+mergedFilename.substring(0, mergedFilename.length()-1)+".pdf");
						    if(tmpFile.exists()) {
						    	MapFileName = mergedFilename.substring(0, mergedFilename.length()-1)+".pdf";
						    	m.put(MapFileName, MapDescription);
						    }
							}catch(Exception e) {
								e.printStackTrace();
							}
						}catch(Exception err) {
							err.printStackTrace();
						}
						
						try {
							src5_with_labels.forEach(ele->{
								String[] resourceurl = ele.attr("href").split("/");
							    String filename = resourceurl[resourceurl.length-1].toString();	
								String filePath = base_dir+"\\"+resourceurl[resourceurl.length-1].toString();
								File file = new File(filePath);
								
								if(file.exists()) {
									file.delete();
								}
							});
						}catch(Exception e) {
							e.printStackTrace();
						}
						
					}
			   }
				
					
					}
	            
	       
		   if("HTML".equalsIgnoreCase(fileType)) {
			   
			   
			   if(url.contains("/Safety/MedWatch/")) {
				 MedWatchSafetyAlerts(url,doc,base_dir);
			   }
			   if(url.contains("/NewsEvents/Speeches/")) {
					 Speeches(url,doc,base_dir);
				   }
			   if(url.contains("/UntitledLetters")) {
					 UntitledLetters(url,doc,base_dir);
				   }
			   if(url.contains("clinicalinvestigators")) {
					 NIDPOE_NOOH_Letters(url,doc,base_dir);
				   }
			   if(url.contains("/WarningLetters/")) {
					 WarningLetters(url,doc,base_dir);
				   }
			   if(url.contains("runningclinicaltrials")) {
					 RestrictionLetters(url,doc,base_dir);
				   }
			   if(url.contains("/MemorandaofUnderstandingMOUs/")){
				   MemorandaOfUnderstanding(url,doc,base_dir);
			       
			   }
			   if(url.contains(".hma")) {
				   WhatsNew(url,doc,base_dir);
			   }
			   if(url.contains("TC/news")) {
				   FDHTML(url,doc,base_dir);
			   }
			   if(url.toLowerCase().contains(".tw/tc")){
				   FDGeneral(url,doc,base_dir);
			   }
			   if(url.contains(".ema.")) {
				   EMHTML(url,doc,base_dir);
				   
			   }
			   else {
				   NewsandEvents(url,doc,base_dir);
			   }
			   
			   
			
                 }
		   return m;
                 }catch(Exception e) {
                	 e.printStackTrace();
                 }
	 			 
	 	
		   return m;
	 		
		       }
			   
		  
		    	
	   
		   
		   
	   public static void main(String[] args) throws Exception {
		
		
		   
	       
	   }
	   


}