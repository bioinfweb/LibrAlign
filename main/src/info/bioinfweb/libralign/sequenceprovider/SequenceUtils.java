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
package info.bioinfweb.libralign.sequenceprovider;


import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.List;



/**
 * Provider general tool methods to be used with instances of {@link SequenceDataProvider}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class SequenceUtils {
	public static <T> List<T> stringToTokenList(String sequence, TokenSet<T> tokenSet) {
		List<T> result = new ArrayList<T>(sequence.length());
		for (int i = 0; i < sequence.length(); i++) {
			result.add(tokenSet.tokenByKeyChar(sequence.charAt(i)));
		}
		return result;
	}
}
