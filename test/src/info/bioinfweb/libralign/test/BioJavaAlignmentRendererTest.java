/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.test;


//Load Java libraries
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
 
//Load BioJava libraries
import org.biojava.bio.*;
import org.biojava.bio.program.abi.ABIFParser;
import org.biojava.bio.program.abi.ABITrace;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.alignment.SimpleAlignment;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.biojava.bio.gui.sequence.*;


/**
 * Class to create an alignment and then display it in a viewer.
 */
public class BioJavaAlignmentRendererTest extends JFrame {
  //Create references to the sequences
  Sequence seq, seq1, seq2, seq3;
 
  //Instantiate the BioJava GUI elements
 
  //TranslatedSequencePanel to hold the renderers 
  TranslatedSequencePanel tsp = new TranslatedSequencePanel();
  //AlignmentRenderer to hold each sequence
  AlignmentRenderer render1, render2, render3;
  //MultiLineRenderer to allow display of multiple tracks in the TranslatedSequencePanel
  MultiLineRenderer multi = new MultiLineRenderer();
  //SymbolSequenceRenderer to handle display of the sequence symbols - only one instance is needed
  SymbolSequenceRenderer symbol = new SymbolSequenceRenderer();
  //RulerRenderer to display sequence coordinates
  RulerRenderer ruler = new RulerRenderer();
  
 
  public BioJavaAlignmentRendererTest(){
    super("Alignment Panel");
 
    //Create the sequences for the alignment
    try {
      seq1 = DNATools.createGappedDNASequence("GAAATCGATCGATAGCTTTTTTTTTTTACGATA-GACTAGCATTCCGAC", "seq1");
      seq2 = DNATools.createGappedDNASequence("GAAATCGATC-ATAGC----------TACGATACGACTAGCATTCCGAC", "seq2");
      seq3 = DNATools.createGappedDNASequence("GAAAT--ATC-ATAGC----------TACGATACGACTAGCATTCCGAC", "seq3");
    }
    catch (BioException bioe) {
      System.err.println("Bioexception: " + bioe);
    }
 
    //Add the sequences to a Map 
    Map<String, Sequence> list = new HashMap();        
    list.put("1", seq1);        
    list.put("2", seq2);        
    list.put("3", seq3);
 
    //Use the Map to create a new SimpleAlignment
    SimpleAlignment ali = new SimpleAlignment((Map) list);
 
    //Instantiate the renderers and set the labels and renderers
    render1 = new AlignmentRenderer();
    render1.setLabel(ali.getLabels().get(0));
    render1.setRenderer(symbol);
 
    render2 = new AlignmentRenderer();  
    render2.setLabel(ali.getLabels().get(1));
    render2.setRenderer(symbol);
 
    render3 = new AlignmentRenderer(); 
    render3.setLabel(ali.getLabels().get(2));
    render3.setRenderer(symbol);
 
    //Add the alignment renderers to the multi-line renderer
    multi.addRenderer(render1);
    multi.addRenderer(render2);
    multi.addRenderer(render3);
 
    // Add trace renderer for testing:
    try {
	    AbiTraceRenderer traceRenderer = new AbiTraceRenderer();
	    traceRenderer.setTrace(new ABITrace(new File("data\\pherograms\\Test_pel1PCR_Pel2Wdhg_PCR-7-A_1.ab1")));
	    traceRenderer.setDepth(200.0);
	    multi.addRenderer(traceRenderer);
    }
    catch (IOException e) {
    	e.printStackTrace();
    }
    
    multi.addRenderer(ruler);

    //Set the sequence in the TranslatedSequencePanel
    tsp.setSequence((SymbolList)ali);
    //Set the background colour of the TranslatedSequencePanel
    tsp.setOpaque(true);
    tsp.setBackground(Color.white);
    //Set the renderer for the TranslatedSequencePanel
    tsp.setRenderer(multi);  
 
    //Set up the display
    Container con = getContentPane();
    con.setLayout(new BorderLayout());
    con.add(tsp, BorderLayout.CENTER);
    setSize(400,200);
    setLocation(100,100);
    setVisible(true);  
  }
 
  /**
   * Main method
   */
  public static void main(String args []){
    new BioJavaAlignmentRendererTest();
  }
}
