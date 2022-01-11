public class CosmicSystem {

    private final String name;
    private MyListNode head;
    private MyListNode tail;

    // Initialises this system as an empty system with a name.
    public CosmicSystem(String name) {
        this.name = name;
    }

    // Adds 'body' to the list of bodies of the system if the list does not already contain a
    // body with the same name as 'body', otherwise does not change the object state. The method
    // returns 'true' if the list was changed as a result of the call and 'false' otherwise.
    public boolean add(Body body) {
        if (body == null) {
            return false;
        }
        if (head == null) {
            tail = head = new MyListNode(body, null, null);
            return true;
        }

        MyListNode newTail = head.add(body);
        if (newTail != null) {
            tail = newTail;
            return true;
        }
        return false;
    }

    // Inserts the specified 'body' at the specified position
    // in this list if 'i' is a valid index and there is no body
    // in the list with the same name as that of 'body'.
    // Shifts the element currently at that position (if any) and
    // any subsequent elements to the right (adds one to their
    // indices). The first element of the list has the index 0.
    // Returns 'true' if the list was changed as a result of
    // the call, 'false' otherwise.
    public boolean add(int i, Body body) {
        if (i < 0 || i > size() || body == null) {
            return false;
        }

        if (this.get(body.getName()) != null) {
            return false;
        }

        if (head == null) {
            if (i == 0) {
                return add(body);
            } else {
                return false;
            }
        }

        if (i == size()) {
            return add(body);
        }

        if (i == 0) {
            head = head.addHead(body);
            return true;
        }

        return head.add(i, body);
    }

    //removes the body at index i from the list, if i is a valid index
    //returns true if removal was done, and false otherwise (invalid index)
    public boolean remove(int i) {
        if (i < 0 || i >= size()) {
            return false;
        }
        return remove(get(i));
    }

    //removes a body from the list, if the list contains a body with the same name as the input body
    //returns true if removal was done, and false otherwise (no body with the same name)
    public boolean remove(Body body) {
        if (head == null || body == null) {
            return false;
        }
        //remove head node
        if (body.getName().equals(head.getBody().getName())) {
            head = head.removeHead();
            return true;
        }
        //remove tail node
        if (body.getName().equals(tail.getBody().getName())) {
            tail = tail.removeTail();
            return true;
        }
        return head.remove(body);
    }

    // Returns the 'body' with the index 'i'. The body that was first added to the list has the
    // index 0, the body that was most recently added to the list has the largest index (size()-1).
    // Precondition: 'i' is a valid index.
    public Body get(int i) {

        return head.get(i);
    }

    // Returns the body with the specified name or 'null' if no such body exits in the list.
    public Body get(String name) {

        if (head == null) {
            return null;
        }
        return head.get(name);
    }

    // Returns the body with the same name as the input body or 'null' if no such body exits in the list.
    public Body get(Body body) {

        if (head == null || body == null) {
            return null;
        }

        return head.get(body.getName());
    }

    // Returns the name of this system.
    public String getName() {
        return name;
    }

    // returns the number of entries of the list.
    public int size() {

        if (head == null) {
            return 0;
        }

        return head.size();
    }

    // Returns a new list that contains the same elements as this list in reverse order. The list 'this'
    // is not changed and only the references to the bodies are copied, not their content (shallow copy).
    public CosmicSystem reverse() {

        CosmicSystem result = new CosmicSystem(this.name);
        if (tail != null) {
            tail.reverse(result);
        }
        return result;
    }

    // Returns a readable representation with the name of the
    // system and all bodies in order of the list.
    // E.g.,
    // Jupiter System:
    // Jupiter, 1.898E27 kg, radius: 6.9911E7 m, position: [0.0,0.0,0.0] m, movement: [0.0,0.0,0.0] m/s.
    // Io, 8.9E22 kg, radius: 1822000.0 m, position: [0.0,0.0,0.0] m, movement: [0.0,0.0,0.0] m/s.
    //
    //Hint: also use toString() in Body.java for this.
    public String toString() {

        String result = this.name;

        if (head != null) {
            result += ":\n" + head.toString();
        } else {
            result += ": EMPTY";
        }

        return result;
    }
}

//class implementing the double-linked list
class MyListNode {
    private final Body b;
    private MyListNode next;
    private MyListNode prev;

    MyListNode(Body b, MyListNode next, MyListNode prev) {
        this.b = b;
        this.next = next;
        this.prev = prev;
    }

    public Body getBody() {
        return this.b;
    }

    MyListNode add(Body b) {
        if (b.getName().equals(this.b.getName())) {
            return null;
        }

        if (next == null) {
            next = new MyListNode(b, null, this);
            return next;
        } else {
            return next.add(b);
        }

    }

    // Precondition: 'i' is a valid index.
    boolean add(int i, Body b) {
        if (i == 0) {
            MyListNode newNode = new MyListNode(b, this, this.prev);
            this.prev.next = this.prev = newNode;
            return true;
        } else {
            if (next != null) {
                return next.add(i - 1, b);
            }
            if (i == 1) {
                return add(b) != null;
            }
        }
        return false;
    }

    MyListNode addHead(Body body) {
        MyListNode newNode = new MyListNode(body, this, null);
        this.prev = newNode;
        return newNode;
    }

    //precondition: body is contained in an inner node
    boolean remove(Body body) {
        MyListNode removeNode = this.next;
        while (removeNode != null && !removeNode.b.getName().equals(body.getName())) {
            removeNode = removeNode.next;
        }
        if (removeNode != null) {
            //inner node
            removeNode.prev.next = removeNode.next;
            removeNode.next.prev = removeNode.prev;
            return true;
        }
        return false;
    }

    MyListNode removeHead() {
        if (this.next != null) {
            this.next.prev = null;
        }
        return this.next;
    }

    MyListNode removeTail() {
        if (this.prev != null) {
            this.prev.next = null;
        }
        return this.prev;
    }

    // Precondition: 'i' is a valid index.
    Body get(int i) {
        if (i == 0) {
            return b;
        } else {
            return next.get(i - 1);
        }

    }

    Body get(String name) {
        if (b.getName().equals(name)) {
            return b;
        }

        if (next == null) {
            return null;
        }
        return next.get(name);

    }

    void reverse(CosmicSystem system) {
        system.add(this.b);
        if (prev != null) {
            prev.reverse(system);
        }
    }

    int size() {
        if (next == null) {
            return 1;
        }
        return 1 + next.size();

    }

    public String toString() {
        return next == null ? this.b.toString() : this.b + "\n" + next.toString();

    }

}