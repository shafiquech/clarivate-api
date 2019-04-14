package com.example.filedemo.service;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.TextPosition;

//import com.example.filedemo.service.myPDFTextStripper;

public class myPDFTextStripperByArea extends myPDFTextStripper {

	private final List<String> regions = new ArrayList<>();
	private final Map<String, Rectangle2D> regionArea = new HashMap<>();
	private final Map<String, ArrayList<List<TextPosition>>> regionCharacterList = new HashMap<>();
	private final Map<String, StringWriter> regionText = new HashMap<>();

	public myPDFTextStripperByArea() throws IOException {
		super.setShouldSeparateByBeads(false);
	}

	@Override
	public final void setShouldSeparateByBeads(boolean aShouldSeparateByBeads) {
	}

	public void addRegion(String regionName, Rectangle2D rect) {
		regions.add(regionName);
		regionArea.put(regionName, rect);
	}

	public List<String> getRegions() {
		return regions;
	}

	public String getTextForRegion(String regionName) {
		StringWriter text = regionText.get(regionName);
		return text.toString();
	}

	public void extractRegions(PDPage page) throws IOException {
		for (String region : regions) {
			setStartPage(getCurrentPageNo());
			setEndPage(getCurrentPageNo());
			String regionName = region;
			ArrayList<List<TextPosition>> regionCharactersByArticle = new ArrayList<>();
			regionCharactersByArticle.add(new ArrayList<TextPosition>());
			regionCharacterList.put(regionName, regionCharactersByArticle);
			regionText.put(regionName, new StringWriter());
		}

		if (page.hasContents()) {
			processPage(page);
		}
	}

	@Override
	protected void processTextPosition(TextPosition text) {
		for (String region : regionArea.keySet()) {
			Rectangle2D rect = regionArea.get(region);
			if (rect.contains(text.getX(), text.getY())) {
				charactersByArticle = regionCharacterList.get(region);
				super.processTextPosition(text);
			}
		}
	}

	@Override
	protected void writePage() throws IOException {
		for (String region : regionArea.keySet()) {
			charactersByArticle = regionCharacterList.get(region);
			output = regionText.get(region);
			super.writePage();
		}
	}
}
