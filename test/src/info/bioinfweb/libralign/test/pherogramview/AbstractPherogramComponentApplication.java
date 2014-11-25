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
package info.bioinfweb.libralign.test.pherogramview;


import info.bioinfweb.libralign.pherogram.PherogramTraceCurveView;
import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;

import java.io.File;
import java.io.IOException;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;



public class AbstractPherogramComponentApplication {
	private PherogramTraceCurveView pherogramView = null;

	
	protected PherogramTraceCurveView getPherogramComponent() throws UnsupportedChromatogramFormatException, IOException {
		if (pherogramView == null) {
			pherogramView = new PherogramTraceCurveView();
//		pherogramView.setProvider(new BioJavaPherogramProvider(ChromatogramFactory.create(
//		new File("data\\pherograms\\Test_pel1PCR_Pel2Wdhg_PCR-7-A_1.ab1"))));
  		pherogramView.setProvider(new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramView.setHorizontalScale(1);			
			pherogramView.setVerticalScale(100);
			pherogramView.getFormats().setQualityOutputType(QualityOutputType.ALL);
			pherogramView.getFormats().setShowProbabilityValues(true);
		}
		return pherogramView;
	}
}
