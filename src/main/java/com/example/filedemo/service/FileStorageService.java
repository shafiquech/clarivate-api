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
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.example.filedemo.service.myPDFTextStripperByArea;

import com.example.filedemo.service.myPDFTextStripper;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
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
				newFilePath = FileStorageService.removeHyperlinks(fileName, targetLocation);
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

			// remove all annotations from the page regardless of type
			pdfPage.getPdfObject().remove(PdfName.Annots);
		}
		pdfDoc.close();
		System.out.println(" linkFilenamePath " + linkFilenamePath);
		return linkFilenamePath = FileStorageService.addBookmarks(linkFilenamePath);

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
		String base_dir = "C:\\Users\\bpatoliya\\Desktop\\Project\\Test\\Test1";
		String fileName = "UCM073005.pdf";

		try {
			String file_name = FileStorageService.removeHyperlinks(fileName, Paths.get(base_dir));
			System.out.println(file_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
