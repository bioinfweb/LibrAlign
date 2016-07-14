/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.pherogram.distortion;


import info.bioinfweb.libralign.pherogram.PherogramUtils;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;



/**
 * Implementation that does not scale the pherogram or insert any gaps. (Used e.g. with {@link PherogramTraceCurveView}).
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class UnscaledPherogramDistortion implements PherogramDistortion {
	private double horizontalScale;
	private PherogramProvider provider;
	

	public UnscaledPherogramDistortion(double horizontalScale, PherogramProvider provider) {
		super();
		this.horizontalScale = horizontalScale;
		this.provider = provider;
	}


	@Override
	public double getHorizontalScale(int baseCallIndex) {
		return horizontalScale;
	}

	
	@Override
	public double getPaintStartX(int baseCallIndex) {
		return PherogramUtils.getFirstTracePosition(provider, baseCallIndex) * horizontalScale;
	}


	@Override
	public double getPaintCenterX(int baseCallIndex) {
		return provider.getBaseCallPosition(baseCallIndex) * horizontalScale;
	}

	
	@Override
	public GapPattern getGapPattern(int baseCallIndex) {
		return null;
	}
}
