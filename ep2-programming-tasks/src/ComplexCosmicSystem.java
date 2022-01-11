public class ComplexCosmicSystem implements CosmicComponent, BodyIterable, CosmicSystemIndex {

    private final String name;
    // A 'dummy' nil node with no content to simplify the remove operation.
    private MyCosmicComponentNode nil = new MyCosmicComponentNode(null, null, null);

    // Initialises this system with a name and at least two cosmic components.
    // Precondition: c1, c2 and all entries in ci are not null
    public ComplexCosmicSystem(String name, CosmicComponent c1, CosmicComponent c2,
                               CosmicComponent... ci) {

        this.name = name;
        this.add(c1);
        this.add(c2);

        for (CosmicComponent c : ci) {
            this.add(c);
        }
    }

    // Adds 'comp' to the list of cosmic components of the system if the list does not already contain a
    // component with the same name as 'comp', otherwise does not change the object state. The method
    // returns 'true' if the list was changed as a result of the call and 'false' otherwise.
    public boolean add(CosmicComponent comp) {
        if (comp == null) {
            return false;
        }

        return nil.add(comp);

    }

    // Returns the name of this system.
    public String getName() {
        return name;
    }



    //Returns the overall number of bodies (i.e. objects of type 'Body') contained in the ComplexCosmicSystem
    //For instance, the System "Solar System{Sun, Earth System{Earth, Moon}, Jupiter System{Jupiter}" contains 4 bodies (Sun, Earth, Moon and Jupiter).
    //
    //Constraint: use the concept of dynamic binding to fulfill this task, i.e. don't use type casts, getClass() or instanceOf()
    public int numberOfBodies() {
        return nil.numberOfBodies();
    }

    // Returns a readable representation with the name of the
    // system and all bodies in respective order of the list.
    public String toString() {
        String result = this.name + "{";

        MyCosmicComponentNode nextNode = nil.next();
        while (nextNode != null) {
            result += nextNode.getComponent();
            nextNode = nextNode.next();
            result += nextNode == null ? "" : ", ";
        }

        result += "}";

        return result;
    }

    //Two objects of type `ComplexCosmicSystem` are equal, if they contain the same bodies (regardless of their order)
    // and the same subsystems.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexCosmicSystem that = (ComplexCosmicSystem) o;

        if (numberOfBodies() != that.numberOfBodies()) return false;

        MyCosmicComponentNode curr = nil.next();
        while (curr != null) {
            if (!that.contains(curr.getComponent())) {
                return false;
            }
            curr = curr.next();
        }

        return true;
    }

    private boolean contains(CosmicComponent c) {
        MyCosmicComponentNode curr = getHead();
        while (curr != null) {
            if (c.equals(curr.getComponent())) {
                return true;
            }
            curr = curr.next();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        MyCosmicComponentNode curr = getHead();
        while (curr != null) {
            hash += curr.getComponent().hashCode();
            curr = curr.next();
        }

        return hash;
    }

    public double getMass() {
        MyCosmicComponentNode nextNode = nil.next();
        double mass = 0;
        while (nextNode != null) {
            mass += nextNode.getComponent().getMass();
            nextNode = nextNode.next();
        }
        return mass;
    }

    public Vector3 getMassCenter() {
        MyCosmicComponentNode nextNode = nil.next();
        Vector3 v = new Vector3(0, 0, 0);
        while (nextNode != null) {
            v = v.plus(nextNode.getComponent().getMassCenter().times(nextNode.getComponent().getMass()));
            nextNode = nextNode.next();
        }
        if (this.getMass() != 0) {
            v = v.times((1 / this.getMass()));
        }
        return v;
    }

    // Returns an iterator over elements of type 'Body'.
    @Override
    public BodyIterator iterator() {
        MyBodyIter iterator = new MyBodyIter(null, null);
        if (nil.next() != null) {
            iterator = nil.next().iterator(iterator);
        }
        return iterator;
    }

    // Returns the 'ComplexCosmicSystem' with which a body is
    // associated. If 'b' is not contained as a key, 'null'
    // is returned.
    @Override
    public ComplexCosmicSystem getParent(Body b) {
        if (!contains(b)) return null;

        MyCosmicComponentNode current = nil.next();
        CosmicComponent currentComponent;

        while (current != null) {
            currentComponent = current.getComponent();
            if (currentComponent.numberOfBodies() == 1) { //component is of type 'Body'
                if (currentComponent.equals(b)) {
                    return this;
                }
            } else if (currentComponent.numberOfBodies() > 1) { //component is of type 'ComplexCosmicsystem'
                CosmicSystemIndex csi = (CosmicSystemIndex) currentComponent;

                if (csi.contains(b)) {
                    return csi.getParent(b);
                }
            } else return null;

            current = current.next();
        }
        return null;
    }

    // Returns 'true' if the specified 'b' is listed
    // in the index.
    @Override
    public boolean contains(Body b) {
        BodyIterator iter = iterator();
        Body curr;

        while (iter.hasNext()) {
            curr = iter.next();
            if (curr.equals(b)) {
                return true;
            }
        }

        return false;
    }

    public BodyCollection getBodies() {

        return new BodyCollection() {

            public boolean add(Body b) {
                return false;
            }

            public boolean contains(Body b) {
                return ComplexCosmicSystem.this.contains(b);
            }

            public int size() {

                return ComplexCosmicSystem.this.numberOfBodies();
            }

            public BodyIterator iterator() {
                return ComplexCosmicSystem.this.iterator();
            }

            public Body[] toArray() {
                Body[] result = new Body[ComplexCosmicSystem.this.numberOfBodies()];
                int i = 0;
                for(Body b: this) {
                    result[i++] = b;
                }
                return result;
            }
        };

    }

    MyCosmicComponentNode getHead() {
        return nil.next();
    }


}

class MyCosmicComponentNode {
    private CosmicComponent c;
    private MyCosmicComponentNode next;
    private MyCosmicComponentNode prev;

    MyCosmicComponentNode(CosmicComponent c, MyCosmicComponentNode next, MyCosmicComponentNode prev) {
        this.c = c;
        this.next = next;
        this.prev = prev;
    }

    public CosmicComponent getComponent() {
        return c;
    }

    public MyCosmicComponentNode next() {
        return next;
    }

    boolean add(CosmicComponent c) {

        if (c.equals(this.c)) {
            return false;
        }

        if (next == null) {
            next = new MyCosmicComponentNode(c, null, this);
            return true;
        } else {
            return next.add(c);
        }

    }

    int numberOfBodies() {

        if (next == null) {
            return this.c == null ? 0 : this.getComponent().numberOfBodies();
        }
        return (this.c == null ? 0 : this.getComponent().numberOfBodies()) + next.numberOfBodies();
    }

    public String toString() {

        return next == null ?  this.c.toString() :
                this.c.toString() + "\n\t" + next.toString();

    }

    // Returns an iterator over all keys of the system of which 'this' is the nil node.
    // 'parent' is the iterator of the parent (path from the nil of root system).
    public MyBodyIter iterator(MyBodyIter next) {

        if (this.c.getClass() == ComplexCosmicSystem.class) {
            if (((ComplexCosmicSystem) this.c).getHead() == null) return new MyBodyIter(null,
                    next);
            return ((ComplexCosmicSystem) this.c).getHead().iterator(new MyBodyIter(this, next));
        } else {
            return new MyBodyIter(this, next);
        }

    }

    public void remove() {
        if (this.next != null) {
            this.next.prev = this.prev;
        }
        if (this.prev != null) {
            this.prev.next = this.next;
        }
    }

    public MyBodyIter nextStep(MyBodyIter next) {
        if (this.next != null) {
            return this.next.iterator(next);
        } else {
            return next;
        }
    }

    public void setNext(MyCosmicComponentNode n) {
        this.next = n;
    }

}