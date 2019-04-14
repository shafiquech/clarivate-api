package com.example.filedemo.service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
public class myPDFTextStripper extends PDFTextStripper {

	public myPDFTextStripper() throws IOException {
		super();
	}

	public ArrayList<List<TextPosition>> getCharactersByArticle() {
		return this.charactersByArticle;
	}

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

		for (TextPosition position : textPositions) {
			String baseFont = position.getFont().getName();
			if (baseFont.contains("Bold"))
				writeString(position.getUnicode().toString());
		}
	}
}
