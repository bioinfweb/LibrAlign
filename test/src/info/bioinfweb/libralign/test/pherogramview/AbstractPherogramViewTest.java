/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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


import java.io.File;

import org.biojava.bio.chromatogram.ChromatogramFactory;

import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;
import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;
import info.bioinfweb.libralign.pherogram.view.PherogramView;



public class AbstractPherogramViewTest {
	private PherogramView pherogramView;

	
	protected PherogramView getPherogramView() {
		if (pherogramView == null) {
			try {
				pherogramView = new PherogramView();
	  		pherogramView.getTraceCurveView().setModel(new PherogramComponentModel(new BioJavaPherogramProvider(
	  				ChromatogramFactory.create(new File("data\\pherograms\\Test_qualityScore.scf")))));
				pherogramView.getTraceCurveView().setHorizontalScale(1);			
				pherogramView.getTraceCurveView().setVerticalScale(100);
				pherogramView.getTraceCurveView().getFormats().setQualityOutputType(QualityOutputType.ALL);
				pherogramView.getTraceCurveView().getFormats().setShowProbabilityValues(true);
			}
			catch (Exception e) {
				throw new InternalError(e);
			}
		}
		return pherogramView;
	}
	
}
