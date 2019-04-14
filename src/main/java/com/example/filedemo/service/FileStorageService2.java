package com.example.filedemo.service;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;

@Service
public class FileStorageService2 {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService2(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename().toLowerCase());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			} else if (!fileName.contains(".pdf")) {
				throw new FileStorageException(
						"Sorry! Please upload only Pdf file. This file " + fileName + " is not a Pdf");
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			String newFilePath = null;

			try {
				newFilePath = FileStorageService2.removeHyperlinks(fileName, targetLocation);
				Files.delete(targetLocation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Paths.get(newFilePath).getFileName().toString();
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public static String removeHyperlinks(String file, Path srcLocation) throws Exception {
		String destFilename = file.substring(0, file.lastIndexOf('.')) + "_Mac.pdf";
		String linkFilenamePath = srcLocation.toString() + "\\" + destFilename;
		System.out.println(file + " <<removeHyperlinks>> srcLocation " + srcLocation);
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcLocation.toString() + "\\" + file),
				new PdfWriter(linkFilenamePath));

		for (int page = 1; page <= pdfDoc.getNumberOfPages(); ++page) {
			PdfPage pdfPage = pdfDoc.getPage(page);
			
			for (PdfAnnotation a : pdfPage.getAnnotations()) {
				PdfLinkAnnotation a1 = (PdfLinkAnnotation)a;
				//Make sure this annotation has a link
		        if (!a1.getSubtype().equals(PdfName.Link))
		            continue;
		        //Make sure this annotation has an ACTION
		        if (a1.getAction() != null) {
		            //Get the ACTION for the current annotation
		            PdfDictionary annotAction = a1.getAction();
		            // Test if it is a URI action (There are tons of other types of actions,
		            // some of which might mimic URI, such as JavaScript,
		            // but those need to be handled seperately)
		            if (annotAction.get(PdfName.S).equals(PdfName.URI) || annotAction.get(PdfName.a).equals(PdfName.URI) ||
		                annotAction.get(PdfName.S).equals(PdfName.GoToR)) {
		                    //do smth with external links
		                    PdfString destination = annotAction.getAsString(PdfName.URI);
		                    PdfString link = annotAction.getAsString(PdfName.S);
		                 
		                    //System.out.println(destination+" ********************* "+link);
		            }
		            else if (annotAction.get(PdfName.S).equals(PdfName.GoTo) ||
		                annotAction.get(PdfName.S).equals(PdfName.GoToE)) {
		                    //do smth with internal links
		            }
		        }
		    }
			
			for (PdfAnnotation a1 : pdfPage.getAnnotations()) {
	
				a1 = (PdfLinkAnnotation)a1;
				
				PdfArray arr = a1.getRectangle();
				Rectangle rect = new Rectangle(((PdfNumber) arr.get(0)).floatValue(),
						((PdfNumber) arr.get(1)).floatValue(),
						((PdfNumber) arr.get(2)).floatValue(),
						((PdfNumber) arr.get(3)).floatValue());
				
				PdfAnnotation paa = new PdfTextAnnotation(rect);
				com.itextpdf.kernel.colors.Color myColor = new DeviceRgb(255, 200, 20);
				
			
			
				//paa = a1;
				paa.setColor(myColor);
				
				//a1 = paa;
				pdfPage.removeAnnotation(a1);
				pdfPage.addAnnotation(paa);
				
				//curAnnot.put(PdfName.C, new PdfArray(new float[]{1, 0, 0}));
				
				//myAnns.add(paa);
				
				
			    //a1.put(PdfName.RC, new PdfNumber(0));
	            //a1.setAppearanceState(PdfName.CA);
	         
	            //a1.setAppearance(PdfAnnotation.HIGHLIGHT_NONE, pd);
				
				/*a1.setFlag(PdfAnnotation.HIDDEN);
				PdfString ps = new PdfString("This is test title ........");
				a1.setTitle(ps);
				PdfString ps2 = new PdfString("Thi is connnnnntecttttt.");
				a1.setContents(ps2);*/
	            //pdfPage.removeAnnotation(a1);
	            //pdfPage.addAnnotation(paa);
			}
			
		
			
			
			
			// remove all annotations from the page regardless of type
			
			//pdfPage.getPdfObject().remove(PdfName.Annots);
			
			
		}
		pdfDoc.close();
		System.out.println(" linkFilenamePath " + linkFilenamePath);
		return linkFilenamePath;
		//return linkFilenamePath = FileStorageService.addBookmarks(linkFilenamePath);

	}

	public static String addBookmarks(String targetLocation) throws Exception {
		File file = new File(targetLocation);

		PDDocument document = PDDocument.load(file);

		PDDocumentOutline outline = new PDDocumentOutline();
		document.getDocumentCatalog().setDocumentOutline(outline);
		PDOutlineItem maintopic = null;
		PDOutlineItem subtopic = null;
		PDOutlineItem title = null;

		myPDFTextStripper pdfStripper = new myPDFTextStripper();

		pdfStripper.setStartPage(1);
		pdfStripper.setEndPage(1);
		String pageText = pdfStripper.getText(document);

		pdfStripper.setStartPage(2);
		pdfStripper.setEndPage(2);
		String pageText1 = pdfStripper.getText(document);
		pdfStripper.setStartPage(3);
		pdfStripper.setEndPage(3);
		String pageText2 = pdfStripper.getText(document);
		if (pageText.contains("CONTENTS*") || pageText.contains("Contents*")
				|| pageText.contains("Table of contents")) {
			return PDF_Outlines(2, document, pdfStripper, outline, maintopic, title, subtopic, targetLocation);
		} else if (pageText.contains("CONTENTS") || pageText.contains("Contents")) {
			return PDF_Outlines_Without_Numbers(1, document, pdfStripper, outline, maintopic, title, subtopic,
					targetLocation);
		} else if (pageText1.contains("CONTENTS") || pageText1.contains("Contents")
				|| pageText1.contains("Table of contents")) {
			return PDF_Outlines(3, document, pdfStripper, outline, maintopic, title, subtopic, targetLocation);
		} else if (pageText2.contains("CONTENTS") || pageText2.contains("Contents")
				|| pageText2.contains("Table of contents")) {
			return PDF_Outlines(4, document, pdfStripper, outline, maintopic, title, subtopic, targetLocation);
		} else {
			return PDF_Outlines_Without_Numbers(1, document, pdfStripper, outline, maintopic, title, subtopic,
					targetLocation);
		}
	}

	public static String PDF_Outlines(int i, PDDocument document, myPDFTextStripper pdfStripper,
			PDDocumentOutline outline, PDOutlineItem maintopic, PDOutlineItem title, PDOutlineItem subtopic,
			String targetLocation) throws IOException {

		File bookmarked = new File(targetLocation);

		while (i < document.getNumberOfPages()) {
			pdfStripper.setStartPage(i);
			pdfStripper.setEndPage(i);

			String boldLines = pdfStripper.getText(document);
			PDPage page = document.getPage(i);

			String[] lines = boldLines.split("\r\n|\r|\n");

			for (String temp : lines) {
				temp = temp.trim();
				if (temp.length() <= 0)
					continue;
				// Using regex checking for main title
				if (temp.matches("^[A-Z0-9]{1,3}\\.? *[A-Z].*") && temp.equals(temp.toUpperCase()) && temp.length() > 10
						&& !temp.contains("ML") && !temp.contains("GM") && !temp.contains("MG")) {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					maintopic = new PDOutlineItem();
					maintopic.setDestination(dest);
					maintopic.setTitle(temp);
					outline.addLast(maintopic);
					// Using regex checking for sub title
				} else if (temp.matches("^(Article).*")) {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					PDOutlineItem Article_title = new PDOutlineItem();
					Article_title.setDestination(dest);
					Article_title.setTitle(temp);
					outline.addLast(Article_title);
					// Using regex checking for sub title
				} else if (temp.matches("^[A-Z0-9]{1,3}\\.+ *[A-Z]{1}.*") && temp.length() > 10 && !temp.contains("ML")
						&& !temp.contains("GM") && !temp.contains("MG")) {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					title = new PDOutlineItem();
					title.setDestination(dest);
					title.setTitle(temp);
					if (maintopic != null) {
						maintopic.addLast(title);
					} else {
						outline.addLast(title);
					}
					// Using regex checking for sub title
				} else if (temp.matches("^\\d+\\.\\d+\\.? *[A-Z]{1}.*") && !temp.contains("ML") && !temp.contains("GM")
						&& !temp.contains("MG")) {

					System.out.println("  " + temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));

					subtopic = new PDOutlineItem();
					subtopic.setDestination(dest);
					subtopic.setTitle(temp);
					if (maintopic != null) {
						maintopic.addLast(subtopic);
					} else if (title != null) {
						title.addLast(subtopic);
					} else {
						outline.addLast(subtopic);
					}
				} else if (temp.matches("^\\d+\\.\\d+\\.\\d+.? *[A-Z]{1}.*")) {

					System.out.println("  " + temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));

					PDOutlineItem bookmark = new PDOutlineItem();
					bookmark.setDestination(dest);
					bookmark.setTitle(temp);
					if (subtopic != null) {
						subtopic.addLast(bookmark);
					} else {
						maintopic.addLast(subtopic);
					}

				}

			}

			i++;
			outline.openNode();
			document.save(bookmarked);
		}
		document.close();
		return bookmarked.getName();
	}

	public static String PDF_Outlines_Without_Numbers(int i, PDDocument document, myPDFTextStripper pdfStripper,
			PDDocumentOutline outline, PDOutlineItem maintopic, PDOutlineItem title, PDOutlineItem subtopic,
			String targetLocation) throws IOException {

		File bookmarked = new File(targetLocation);

		while (i < document.getNumberOfPages() + 1) {
			if (document.getNumberOfPages() <= 2)
				break;
			pdfStripper.setStartPage(i);
			pdfStripper.setEndPage(i);

			String pageText2 = pdfStripper.getText(document);
			if (pageText2.contains("CONTENT") || pageText2.contains("Content")) {
				i++;
				pdfStripper.setStartPage(i);
				pdfStripper.setEndPage(i);
			}

			Rectangle2D region = new Rectangle2D.Double(0, 70, 595, 600);
			String regionName = "region";
			myPDFTextStripperByArea stripper = new myPDFTextStripperByArea();
			PDPage page = document.getPage(i - 1);
			stripper.addRegion(regionName, region);
			stripper.extractRegions(page);
			String boldLines = stripper.getTextForRegion(regionName);
			PDResources res = page.getResources();

			String[] lines = boldLines.split("\r\n|\r|\n");

			for (String temp : lines) {
				temp = temp.trim();
				if (temp.length() <= 0)
					continue;
				if (!Charset.forName("US-ASCII").newEncoder().canEncode(temp)) {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					maintopic = new PDOutlineItem();
					maintopic.setDestination(dest);
					maintopic.setTitle(temp);
					outline.addLast(maintopic);
				}

				// Using regex checking for main title

				if (temp.matches(".*[A-Z].*") && temp.equals(temp.toUpperCase()) && temp.length() > 5
						&& !temp.startsWith("\"") && !temp.endsWith("\"") && !temp.contains("...")
						&& !temp.contains("---") && temp.charAt(0) != '“' && temp.charAt(temp.length() - 1) != '”') {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					maintopic = new PDOutlineItem();
					maintopic.setDestination(dest);
					maintopic.setTitle(temp);
					outline.addLast(maintopic);
					// Using regex checking for sub title
				} else if (temp.matches("^(REGULATION).*")) {
					System.out.println(temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));
					maintopic = new PDOutlineItem();
					maintopic.setDestination(dest);
					maintopic.setTitle(temp);
					outline.addLast(maintopic);
				} else if (temp.matches("^[A-Z0-9a-z]{1,3}\\.{1}\\s+[A-Z]{1}.*") && !temp.contains("...")
						&& !temp.contains("---")) {

					System.out.println("  " + temp);
					PDPageDestination dest = new PDPageFitWidthDestination();
					dest.setPage(document.getPage(i - 1));

					PDOutlineItem bookmark = new PDOutlineItem();
					bookmark.setDestination(dest);
					bookmark.setTitle(temp);
					if (maintopic != null) {
						maintopic.addLast(bookmark);
					} else {
						outline.addLast(bookmark);
					}
				}

			}
			i++;
			outline.openNode();
			document.save(bookmarked);
		}
		document.close();
		return bookmarked.getName();
	}

	public static void main(String[] args) throws Exception, IOException {
		String base_dir = "C:\\Users\\shafique\\Desktop";
		String fileName = "ENCePPActivities2012.pdf";

		try {
			String file_name = FileStorageService2.removeHyperlinks(fileName, Paths.get(base_dir));
			System.out.println(file_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
