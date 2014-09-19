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


import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.nexml.model.CharacterStateSet;
import org.nexml.model.Document;
import org.nexml.model.DocumentFactory;
import org.nexml.model.MolecularMatrix;
import org.nexml.model.OTU;
import org.nexml.model.OTUs;



public class NeXMLTest {
  public static void main(String[] args) {
  	try {
  		Document document = DocumentFactory.createDocument();

  		OTUs otus = document.createOTUs();
  		OTU otu1 = otus.createOTU();
  		otu1.setLabel("Taxon A");
  		OTU otu2 = otus.createOTU();
  		otu2.setLabel("Taxon B");
  		OTU otu3 = otus.createOTU();
  		otu3.setLabel("Taxon C");
  		
  		MolecularMatrix matrix = document.createMolecularMatrix(otus, "DNA");
  		CharacterStateSet set = matrix.createCharacterStateSet();
  		matrix.setSeq("ACGCTTGA", otu1);
  		matrix.setSeq("ACGC-TGA", otu2);
  		matrix.setSeq("ACCC-TGA", otu3);
  		
  		URI ns = URI.create("http://bioinfweb.info/xmlns/LibrAlign/DataAreas/Pherogram");
    	matrix.getRowObject(otu1).addAnnotationValue("lada_ph:position", ns, 4);
    	
  		String xml = document.getXmlString();
  		System.out.println(xml);
  	}
  	catch (ParserConfigurationException e) {
  		e.printStackTrace();
  	}
  	
	}	
}
