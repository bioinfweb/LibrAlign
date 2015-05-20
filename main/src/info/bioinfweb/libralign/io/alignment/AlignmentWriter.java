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
package info.bioinfweb.libralign.io.alignment;


import info.bioinfweb.libralign.model.AlignmentModel;

import java.io.File;
import java.io.OutputStream;



/**
 * Interface to be implemented by all classes writing alignment data provided by an instance of {@link AlignmentModel}
 * in different formats to streams of files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface AlignmentWriter {
	/**
	 * Writes the contents of {@code provider} into the specified stream.
	 * 
	 * @param stream - the stream to write the alignment data to
	 * @param provider - the sequence data provider containing the alignment data
	 * @throws Exception depending on the implementing class
	 */
	public void write(OutputStream stream, AlignmentModel<?> provider) throws Exception;

	/**
	 * Writes the contents of {@code provider} into the specified file.
	 * 
	 * @param file - the file to write the alignment data to
	 * @param provider - the sequence data provider containing the alignment data
	 * @throws Exception IO exceptions thrown while trying to write the file or possibly additional exceptions
	 *         of other types depending on the implementing class
	 */
	public void write(File file, AlignmentModel<?> provider) throws Exception;
}
