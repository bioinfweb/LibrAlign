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
package info.bioinfweb.libralign.dataarea;


import info.bioinfweb.commons.collections.ListChangeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



/**
 * A wrapper around any implementation of {@link List} that fires {@link DataAreaChangeEvent}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class DataAreaChangeEventList implements List<DataArea> {
	private DataAreaList dataAreaList;
  private List<DataArea> underlyingList;

  
	/**
	 * This constructor is only meant to be used in {@link DataAreaList}. It creates a new instance of this 
	 * class using the class itself as the data area list. 
	 * 
	 * @param underlyingList - the list object that stored the data areas
	 */
	protected DataAreaChangeEventList(List<DataArea> underlyingList) {
		super();
		this.dataAreaList = (DataAreaList)this;
		this.underlyingList = underlyingList;
	}


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param dataAreaList - the data area list object that contains the data areas that will be inserted
	 *        removed or replaced by the new instance 
	 * @param underlyingList - the list object that stored the data areas
	 */
	public DataAreaChangeEventList(DataAreaList dataAreaList,	List<DataArea> underlyingList) {
		super();
		this.dataAreaList = dataAreaList;
		this.underlyingList = underlyingList;
	}


	public DataAreaList getDataAreaList() {
		return dataAreaList;
	}


	protected List<DataArea> getUnderlyingList() {
		return underlyingList;
	}


	/**
	 * Appends the specified data area to the end of this list. This list is automatically set as
	 * the owning list of {@code element}.
	 * 
	 * @param element - the element to be added to the list
	 */
	@Override
	public boolean add(DataArea element) {
		boolean result = getUnderlyingList().add(element);
		if (result) {
			element.setList(getDataAreaList());
			getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, element);
		}
		return result;
	}
	

	@Override
	public void add(int index, DataArea element) {
		element.setList(getDataAreaList());
		getUnderlyingList().add(index, element);
		getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, element);
	}
	

	@Override
	public boolean addAll(Collection<? extends DataArea> c) {
		return addAll(size(), c);
	}
	

	@Override
	public boolean addAll(int index, Collection<? extends DataArea> c) {
		boolean result = getUnderlyingList().addAll(index, c);
		if (result) {
			Iterator<? extends DataArea> iterator = c.iterator();
			while (iterator.hasNext()) {
				iterator.next().setList(getDataAreaList());
			}
			getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, c);
		}
		return result;
	}

	
	@Override
	public void clear() {
		List<DataArea> copy = new ArrayList<DataArea>(size());  // Clone cannot be used here, because changes there also affect the original list.
		copy.addAll(this);
		getUnderlyingList().clear();
		getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, copy);
	}
	

	@Override
	public DataArea remove(int index) {
		DataArea result = getUnderlyingList().remove(index);
		getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.DELETION, result);
		return result;
	}
	

	@Override
	public boolean remove(Object o) {
		boolean result = getUnderlyingList().remove(o);
		if (result) {
			getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.DELETION, (DataArea)o);
		}
		return result;
	}
	

	@Override
	public boolean removeAll(Collection<?> c) {
		List<DataArea> removedElements = new ArrayList<DataArea>(size());  // c cannot be used here, because it may contain elements which are not contained in this instance.
		removedElements.addAll(this);
		boolean result = getUnderlyingList().removeAll(c);
		if (result) {
			removedElements.retainAll(c);
			getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.DELETION, removedElements);
		}
		return result;
	}
	

	@Override
	public boolean retainAll(Collection<?> c) {
		List<DataArea> removedElements = new ArrayList<DataArea>(size());  // Clone cannot be used here, because changes there also affect the original list.
		removedElements.addAll(this);
		boolean result = getUnderlyingList().retainAll(c);
		if (result) {
			removedElements.removeAll(c);
			getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.DELETION, removedElements);
		}
		return result;
	}

	
	@Override
	public DataArea set(int index, DataArea element) {
		DataArea result = getUnderlyingList().set(index, element);
		getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.REPLACEMENT, result);
		return result;
	}


	@Override
	public boolean contains(Object o) {
		return getUnderlyingList().contains(o);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return getUnderlyingList().containsAll(c);
	}


	@Override
	public DataArea get(int index) {
		return getUnderlyingList().get(index);
	}


	@Override
	public int indexOf(Object o) {
		return getUnderlyingList().indexOf(o);
	}


	@Override
	public boolean isEmpty() {
		return getUnderlyingList().isEmpty();
	}


	@Override
	public Iterator<DataArea> iterator() {
		final DataAreaChangeEventList thisList = this;
		final Iterator<DataArea> iterator = getUnderlyingList().iterator();
		return new Iterator<DataArea>() {
					private DataArea currentArea = null;
					
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
		
					@Override
					public DataArea next() {
						currentArea = iterator.next();
						return currentArea;
					}
		
					@Override
					public void remove() {
						iterator.remove();  // Would throw an exception if currentArea would still be null.
						thisList.getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.DELETION, currentArea);
					}
				};
	}


	@Override
	public int lastIndexOf(Object o) {
		return getUnderlyingList().lastIndexOf(o);
	}


	@Override
	public ListIterator<DataArea> listIterator() {
		return listIterator(0);
	}


	@Override
	public ListIterator<DataArea> listIterator(int index) {
		final DataAreaChangeEventList thisList = this;
		final ListIterator<DataArea> iterator = getUnderlyingList().listIterator(index);
		return new ListIterator<DataArea>() {
			    private DataArea current = null;
			
					@Override
					public void add(DataArea e) {
						iterator.add(e);
						thisList.getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, e);
					}
		
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
		
					@Override
					public boolean hasPrevious() {
						return iterator.hasPrevious();
					}
		
					@Override
					public DataArea next() {
						current = iterator.next();
						return current;
					}
		
					@Override
					public int nextIndex() {
						return iterator.nextIndex();
					}
		
					@Override
					public DataArea previous() {
						current = iterator.previous();
						return current;
					}
		
					@Override
					public int previousIndex() {
						return iterator.previousIndex();
					}
		
					@Override
					public void remove() {
						iterator.remove();
						thisList.getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.INSERTION, current);
					}
		
					@Override
					public void set(DataArea e) {
						iterator.set(e);
						thisList.getDataAreaList().getOwner().fireInsertedRemoved(ListChangeType.REPLACEMENT, current);
					}
				};
	}


	@Override
	public int size() {
		return getUnderlyingList().size();
	}


	@Override
	public List<DataArea> subList(int fromIndex, int toIndex) {
		return new DataAreaChangeEventList(getDataAreaList(), getUnderlyingList().subList(fromIndex, toIndex));
	}


	@Override
	public Object[] toArray() {
		return getUnderlyingList().toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		return getUnderlyingList().toArray(a);
	}
}
