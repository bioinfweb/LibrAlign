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
package info.bioinfweb.libralign.model.adapters;


import info.bioinfweb.libralign.model.BasicAlignmentModelView;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * All classes implementing {@link BasicAlignmentModelView}s can inherit from this class,
 * which implements the access to the underlying data source. Although this class is not abstract
 * it does not make sense creating instances of it directly since it implements no functionality.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying provider
 */
public class AbstractAlignmentModelAdapter<T> implements BasicAlignmentModelView<T> {
	private AlignmentModel<T> underlyingModel;

	
	protected AbstractAlignmentModelAdapter(AlignmentModel<T> underlyingProvider) {
		super();
		this.underlyingModel = underlyingProvider;
	}
	

	@Override
	public AlignmentModel<T> getUnderlyingModel() {
		return underlyingModel;
	}

	
	public void setUnderlyingModel(AlignmentModel<T> underlyingProvider) {
		this.underlyingModel = underlyingProvider;
	}
}
