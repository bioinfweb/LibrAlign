/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.sequenceprovider;



/**
 * Specifies if whole sequences or single tokens can be edited in the underlying data source of the
 * implementation of an {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public enum SequenceDataProviderWriteType {
	NONE,
	SEQUENCES_ONLY,
	TOKENS_ONLY,
	BOTH;
}