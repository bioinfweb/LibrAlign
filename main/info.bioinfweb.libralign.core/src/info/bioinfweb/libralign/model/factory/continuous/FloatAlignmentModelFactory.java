/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.model.factory.continuous;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.AbstractAlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.continuous.ContinuousToken;
import info.bioinfweb.libralign.model.tokenset.continuous.FloatTokenSet;



/**
 * Factory that creates {@link ArrayListAlignmentModel}s with {@link Float} values as tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class FloatAlignmentModelFactory extends AbstractAlignmentModelFactory<ContinuousToken<Float>> {
	@Override
	public AlignmentModel<ContinuousToken<Float>> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		return new ArrayListAlignmentModel<ContinuousToken<Float>>(new FloatTokenSet());
	}
}
