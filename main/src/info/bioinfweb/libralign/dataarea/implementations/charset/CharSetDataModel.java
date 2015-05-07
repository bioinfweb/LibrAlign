/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.util.ArrayList;

import info.bioinfweb.libralign.alignmentmodel.DataProvider;



/**
 * The data model for a {@link CharSetArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class CharSetDataModel extends ArrayList<CharSet> implements DataProvider {
	public CharSet getByName(String name) {
		for (CharSet charSet : this) {  //TODO Possibly use a TreeMap for this method in the future to support large numbers of character sets.
			if (charSet.getName().equals(name)) {
				return charSet;
			}
		}
		return null;
	}
}
