package piaf.tools;

import java.util.NoSuchElementException;

/**
 * @author mb
 * 
 * A very simplistic and small FIFO-queue implementation 
 * using a fixed size array 
 */
public class FixedSizeQueue<E> {
	
	private E[] Queue;
	private int First_Element;
	private int Last_Element;
	private int Size;
	private int Max_Size;
	
	@SuppressWarnings("unchecked")
	public FixedSizeQueue(int max_Size) {
		Max_Size      = max_Size;
		Queue         = (E[]) new Object[Max_Size];
		clear();
	}
	
	/**
	 * pushes an object onto the Queue
	 * @param anObject the object
	 * @return boolean success
	 */
    public synchronized boolean push(E anObject) {
    	// add the object to array if space left
    	if (size() < Max_Size) {
	    	Last_Element = (Last_Element + 1 ) % Max_Size;
	    	Queue[Last_Element] = anObject;
	    	Size++;
	    	return true;
    	} else {
    		return false;
    	}
    }
    
	/**
	 * pushes an array of objects onto the Queue
	 * @param objectArray array of objects (max length 127!)
	 * @return boolean success
	 */
    public synchronized boolean pushArray (E[] objectArray) {
    	// add the object to array if space left
    	if ((size() + objectArray.length) < Max_Size) {
    		for(int i = 0; i <  objectArray.length; i++) {
    			push(objectArray[i]);
    		}
	    	return true;
    	} else {
    		return false;
    	}
    }

	/**
	 * fetches an object from the start of the Queue
	 * and removes it
	 * @return Object the object removed from the start of the stock
	 * @throws NoSuchElementException (in order to allow execution with standard java vm)
	 */
    public synchronized E pop() throws NoSuchElementException {
		// get object
		E popped = peek();
		// remove (move First Element number) 
		First_Element = (First_Element + 1 ) % Max_Size;
		Size--;
		// and return object
		return popped;
    } // pop()
	
	/**
	 * fetches an object from the start of the Queue
	 * <br>does not remove it!
	 * @return Object the object at the start of the Queue
	 * @throws NoSuchElementException (in order to allow execution with standard java vm)
	 */
    public synchronized E peek() throws NoSuchElementException {
		// empty Queue?
		if(empty())
	    	throw new NoSuchElementException();
	    // return first element
		return Queue[First_Element];
    } // peek()
    
	/**
	 * Empties the queue 
	 */
    public synchronized void clear() {
		First_Element = 0;
		Last_Element  = -1;
		Size          = 0;
    }
    
	/**
	 * Number of elements in the queue
	 * @return int nr of elements in the queue
	 */
    public int size() {
    	return Size;
    }
    
	/**
	 * Is this queue empty?
	 * @return boolean true, if the queue is empty
	 */
    public boolean empty() {
		return (size() == 0);
    }
    
	/**
	 * Is this queue full?
	 * @return boolean true, if the queue is full
	 */
    public boolean full() {
		return (size() == Max_Size);
    }

}
