/** A linked list of character data objects.
 *  (Actually, a list of Node objects, each holding a reference to a character data object.
 *  However, users of this class are not aware of the Node objects. As far as they are concerned,
 *  the class represents a list of CharData objects. Likwise, the API of the class does not
 *  mention the existence of the Node objects). */
public class List {

    // Points to the first node in this list
    private Node first;

    // The number of elements in this list
    private int size;

    /** Returns the node */
    public Node getNode() {
        return this.first;
    }
	
    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }

    /** Returns the number of elements in this list. */
    public int getSize() {
 	      return size;
    }

    /** Returns the first element in the list */
    public CharData getFirst() {
        return first.cp;
    }

    /** GIVE Adds a CharData object with the given character to the beginning of this list. */
    public void addFirst(char chr) {
        
        Node newNode = new Node(new CharData(chr));

        newNode.next = this.first;

        this.first = newNode;

        this.size++;
    }
    
    /** GIVE Textual representation of this list. */
    public String toString() {

        if (this.size == 0) {
            return "()";
        }

        String str = "(";
        Node current = this.first;
        while (current != null) {
            str += current.cp.toString() + " ";
            current = current.next;
        }

        return str.substring(0, str.length() - 1) + ")";
    }

    /** Returns the index of the first CharData object in this list
     *  that has the same chr value as the given char,
     *  or -1 if there is no such object in this list. */
    public int indexOf(char chr) {
        
        Node current = this.first;
        int index = 0;
        while (current != null) {
            if (current.cp.equals(chr)) {
                return index;
            }
            current = current.next;
            index++;
        }

        return -1; // Value not found
    }

    /** If the given character exists in one of the CharData objects in this list,
     *  increments its counter. Otherwise, adds a new CharData object with the
     *  given chr to the beginning of this list. */
    public void update(char chr) {
        
        if (this.indexOf(chr) != -1) {

            Node current = this.first;

            while (current != null) {
                if (current.cp.equals(chr)) {
                    current.cp.count++;
                }
                current = current.next;
            }
        }
        else {
            this.addFirst(chr);
        }
    }

    /** GIVE If the given character exists in one of the CharData objects
     *  in this list, removes this CharData object from the list and returns
     *  true. Otherwise, returns false. */
    public boolean remove(char chr) {
        // Finds the node to remove, using two pointers;
        // prev is one step behind current
        Node prev = null;
        Node current = first;

        while (current != null && current.cp.chr != chr) {
            prev = current;
            current = current.next;
        }
        if (current == null) return false; // not found
        // Remove the elements. If it's the first element, updates first
        if (prev == null) { // it's the first element
            first = first.next;
        }
        else {
            prev.next = current.next;
        }

        size--;
        return true;
    }

    /** Returns the CharData object at the specified index in this list. 
     *  If the index is negative or is greater than the size of this list, 
     *  throws an IndexOutOfBoundsException. */
    public CharData get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        else {
            Node current = this.first;
            int i = 0;
            CharData c = null;

            while (current != null) {
                if (i == index) {
                    c = current.cp;
                }
                current = current.next;
                index++;
            }

            return c;
        }
    }

    /** Returns an array of CharData objects, containing all the CharData objects in this list. */
    public CharData[] toArray() {
	    CharData[] arr = new CharData[size];
	    Node current = first;
	    int i = 0;
        while (current != null) {
    	    arr[i++]  = current.cp;
    	    current = current.next;
        }
        return arr;
    }

    /** Returns an iterator over the elements in this list, starting at the given index. */
    public ListIterator listIterator(int index) {
	    // If the list is empty, there is nothing to iterate   
	    if (size == 0) return null;
	    // Gets the element in position index of this list
	    Node current = first;
	    int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
	    return new ListIterator(current);
    }
}
