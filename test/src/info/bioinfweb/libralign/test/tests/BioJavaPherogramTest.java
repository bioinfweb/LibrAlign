/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.test.tests;


import java.io.File;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.seq.DNATools;



public class BioJavaPherogramTest {
	public static void main(String[] args) {
		try {
			Chromatogram chromatogram = ChromatogramFactory.create(
					new File("data\\pherograms\\Test_pel1PCR_Pel2Wdhg_PCR-7-A_1.ab1"));
    			//new File("data\\pherograms\\Test_pel1PCR_Pel2Wdhg_PCR-7-A_1.scf"));
			System.out.println(chromatogram.getTraceLength() + " " + chromatogram.getSequenceLength());
			for (int i = 0; i < 100; i++) {
				System.out.println(chromatogram.getTrace(DNATools.a())[i]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
