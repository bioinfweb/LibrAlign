/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.data;



/**
 * Default data model factory implementation that is able to create instances of all data model 
 * classes that offer a constructor with no arguments.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <M> the model class that shall be created by this factory 
 */
public class NoArgDataModelFactory<M extends DataModel<L>, L> implements DataModelFactory<M, L> {
	private Class<M> modelClass;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param modelClass
	 * @throws IllegalArgumentException if the specified class does not offer a public constructor with 
	 *         no arguments 
	 * @throws SecurityException If a security manager, {@code s}, is present and the caller's class loader is not the same 
	 *         as or an ancestor of the class loader for the current class and invocation of {@code s.checkPackageAccess()} 
	 *         denies access to the package of this class.
	 */
	public NoArgDataModelFactory(Class<M> modelClass) throws IllegalArgumentException, SecurityException {
		super();
		try {
			modelClass.getConstructor();  // Tests whether a no arg constructor is available and throws an exception otherwise.
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The specified class does not offer a constructor with no arguments.", e);
		}
		this.modelClass = modelClass;
	}


	@Override
	public M createNewModel() {
		try {
			return modelClass.newInstance();
		} 
		catch (InstantiationException e) {
			throw new InternalError(e);
		} 
		catch (IllegalAccessException e) {
			throw new InternalError(e);
		}
	}
}
