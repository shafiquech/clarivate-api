package com.example.filedemo.service;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.layout.Document;

public class TextAnnotation {    
   public static void main(String args[]) throws Exception {        
      // Creating a PdfWriter       
      String dest = "C:\\Users\\shafique\\Desktop\\textAnnotation.pdf";       
      PdfWriter writer = new PdfWriter(dest);               
      
      // Creating a PdfDocument       
      PdfDocument pdf = new PdfDocument(writer);               
      
      // Creating a Document        
      Document document = new Document(pdf);             
      
      // Creating PdfTextAnnotation object
      Rectangle rect = new Rectangle(20, 800, 0, 0);       
      PdfAnnotation ann = new PdfTextAnnotation(rect);              
      
      // Setting color to the annotation
      float[] color = {0,0.5f,0};
      com.itextpdf.kernel.colors.Color myColor = new DeviceRgb(255, 100, 20);
      ann.setColor(myColor);              
      
      // Setting title to the annotation 
      ann.setTitle(new PdfString("Hello"));              
      
      // Setting contents of the annotation       
      ann.setContents("Hi welcome to Tutorialspoint.");              
      
      // Creating a new page       
      PdfPage page =  pdf.addNewPage();              
      
      // Adding annotation to a page in a PDF
      page.addAnnotation(ann);

      
      PdfAction action = PdfAction.createURI("http://pages.itextpdf.com/ebook-stackoverflow-questions.html");
      
      Rectangle linkLocation1 = new Rectangle(30, 770, 90, 30);

      PdfAnnotation link1 = new PdfLinkAnnotation(linkLocation1)

              .setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)

              .setAction(action)
              .setTitle( new PdfString("This is the link "))
              .setContents(new PdfString("hhhhhhhhhhhhhhhhhhhhhhhhhhh................jjjjjjjjjjjjjjjj. "))
              .setColor(color);

      page.addAnnotation(link1);
      // Closing the document       
      document.close();       
      
      System.out.println("Annotation added successfully");    
   } 
}