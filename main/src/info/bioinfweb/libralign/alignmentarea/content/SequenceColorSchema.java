/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Color;
import java.awt.SystemColor;
import java.util.Map;
import java.util.TreeMap;



/**
 * Class that provides color information for the output of an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceColorSchema {
	public static final Color DEFAULT_GAP_COLOR = Color.GRAY;
	public static final Color DEFAULT_UNKNOWN_COLOR = Color.GRAY.brighter();
	
	
	private Color tokenBorderColor = Color.WHITE;
	private Color defaultBgColor = Color.LIGHT_GRAY.brighter();
	private Color fontColor = Color.BLACK;
	private Color cursorColor = Color.BLACK;
	private float cursorLineWidth = 2f;
	private Color selectionColor = SystemColor.textHighlight;
	private Color selectionFontColor = SystemColor.textHighlightText;
	private Map<String, Color> nucleotideColorMap = createDefaultNucleotideColorMap();
	private Map<String, Color> aminoAcidColorMap = createDefaultAminoAcidColorMap();
	
	
	private static Map<String, Color> createDefaultNucleotideColorMap() {
		Color colorTU = new Color(230, 90, 90);
		Map<String, Color> result = new TreeMap<String, Color>();
		result.put("A", new Color(90, 228, 93));
		result.put("T", colorTU);
		result.put("U", colorTU);
		result.put("C", new Color(90, 90, 230));
		result.put("G", new Color(226, 230, 90));
		result.put("-", DEFAULT_GAP_COLOR);
		result.put("?", DEFAULT_UNKNOWN_COLOR);
		return result;
	}
	
	
	private static Map<String, Color> createDefaultAminoAcidColorMap() {
		Map<String, Color> result = new TreeMap<String, Color>();
		result.put("A", new Color(32, 255, 8));
		result.put("C", new Color(255, 162, 202));
		result.put("D", new Color(220, 134, 38));
		result.put("E", new Color(236, 164, 38));
		result.put("F", new Color(255, 0, 179));
		result.put("G", new Color(100, 137, 179));
		result.put("H", new Color(151, 60, 142));
		result.put("I", new Color(248, 255, 0));
		result.put("K", new Color(0, 196, 252));
		result.put("L", new Color(255, 253, 0));
		result.put("M", new Color(248, 247, 0));
		result.put("N", new Color(197, 109, 105));
		result.put("P", new Color(148, 83, 95));
		result.put("Q", new Color(255, 190, 3));
		result.put("R", new Color(7, 182, 255));
		result.put("S", new Color(0, 255, 12));
		result.put("T", new Color(0, 255, 59));
		result.put("V", new Color(254, 255, 0));
		result.put("W", new Color(255, 0, 29));
		result.put("Y", new Color(235, 0, 173));
		result.put("-", DEFAULT_GAP_COLOR);
		result.put("?", DEFAULT_UNKNOWN_COLOR);
		return result;
	}
	
	
	public Color getTokenBorderColor() {
		return tokenBorderColor;
	}
	
	
	public void setTokenBorderColor(Color tokenBorderColor) {
		this.tokenBorderColor = tokenBorderColor;
	}
	
	
	public Color getDefaultBgColor() {
		return defaultBgColor;
	}
	
	
	public void setDefaultBgColor(Color defaultBgColor) {
		this.defaultBgColor = defaultBgColor;
	}
	
	
	public Color getFontColor() {
		return fontColor;
	}


	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}


	public Color getCursorColor() {
		return cursorColor;
	}


	public void setCursorColor(Color cursorColor) {
		this.cursorColor = cursorColor;
	}


	public float getCursorLineWidth() {
		return cursorLineWidth;
	}


	public void setCursorLineWidth(float cursorLineWidth) {
		this.cursorLineWidth = cursorLineWidth;
	}


	public Color getSelectionColor() {
		return selectionColor;
	}
	
	
	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}
	
	
	public Color getSelectionFontColor() {
		return selectionFontColor;
	}


	public void setSelectionFontColor(Color selectionFontColor) {
		this.selectionFontColor = selectionFontColor;
	}


	public Map<String, Color> getNucleotideColorMap() {
		return nucleotideColorMap;
	}
	
	
	public Map<String, Color> getAminoAcidColorMap() {
		return aminoAcidColorMap;
	}
}
