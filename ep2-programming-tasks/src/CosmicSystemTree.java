//This class represents a binary search tree for objects of class 'CosmicSystem'
public class CosmicSystemTree {

    private MyTreeNode root;

    // Adds a system of bodies to the tree. Since the keys of the tree are the names of bodies,
    // adding a system adds multiple (key, value) pairs to the tree, one for each body of the
    // system, with the same value, i.e., reference to the cosmic system.
    // An attempt to add a system with a body that already exists in the tree
    // leaves the tree unchanged and the returned value would be 'false'.
    // For example, it is not allowed to have a system with the bodies "Earth" and "Moon" and another
    // system with the bodies "Jupiter" and "Moon" indexed by the tree, since "Moon" is not unique.
    // The method returns 'true' if the tree was changed as a result of the call and
    // 'false' otherwise.
    public boolean add(CosmicSystem system) {

        if (system == null || system.size() == 0) {
            return false;
        }

        if (root != null) {
            for (int i = 0; i < system.size(); i++) {
                if (root.get(system.get(i).getName()) != null) {
                    return false;
                }
            }

            root.add(new MyTreeNode(system.get(0).getName(), system, null, null));
        } else {
            root = new MyTreeNode(system.get(0).getName(), system, null, null);
        }

        for (int i = 1; i < system.size(); i++) {
            root.add(new MyTreeNode(system.get(i).getName(), system, null, null));
        }

        return true;

    }

    // Returns the cosmic system in which a body with the specified name exists.
    // For example, if the specified name is "Europa", the system of Jupiter (Jupiter, Io,
    // Europa, Ganymed, Kallisto) will be returned.
    // If no such system is found, 'null' is returned.
    public CosmicSystem get(String name) {
        if (root == null) {
            return null;
        }
        return root.get(name);
    }

    // Returns the overall number of bodies indexed by the tree.
    public int numberOfBodies() {
        if (root == null) {
            return 0;
        }

        return root.numberOfNodes();
    }

    // Returns a readable representation with (key, value) pairs, sorted alphabetically by the key.
    public String toString() {
        if (root == null) {
            return "EMPTY";
        }

        return root.toString();
    }

    //BONUS TASK: sets a new canvas and draws the tree using StdDraw
    public void drawTree() {
        StdDraw.setCanvasSize(1000, 500);
        StdDraw.setXscale(0, 2);
        StdDraw.setYscale(0, 1);

        if (root != null) {
            root.draw(1, 0.95, false);
        }
    }
}

//private class implementing the binary tree
class MyTreeNode {
    private MyTreeNode left;
    private MyTreeNode right;
    private final String key;
    private final CosmicSystem cs;

    MyTreeNode(String key, CosmicSystem cs, MyTreeNode left, MyTreeNode right) {
        this.key = key;
        this.cs = cs;
        this.left = left;
        this.right = right;

    }

    boolean add(MyTreeNode myTreeNode) {
        if (myTreeNode.key.equals(this.key)) {
            return false;
        }

        if (this.key.compareTo(myTreeNode.key) > 0) {
            if (left == null) {
                left = myTreeNode;
                return true;
            } else {
                return left.add(myTreeNode);
            }
        } else {
            if (right == null) {
                right = myTreeNode;
                return true;
            } else {
                return right.add(myTreeNode);
            }
        }

    }

    CosmicSystem get(String name) {
        if (key.equals(name)) {
            return cs;
        }

        if (key.compareTo(name) > 0) {
            if (left == null) {
                return null;
            }
            return left.get(name);
        } else {
            if (right == null) {
                return null;
            }
            return right.get(name);
        }

    }

    int numberOfNodes() {
        int leftNodes = 0;
        int rightNodes = 0;
        if (left != null) {
            leftNodes = left.numberOfNodes();
        }
        if (right != null) {
            rightNodes = right.numberOfNodes();
        }
        return 1 + leftNodes + rightNodes;
    }

    public String toString() {
        String result;
        result = left == null ? "" : left.toString();
        result += "(" + this.key + "," + this.cs.getName() + ")\n";
        result += right == null ? "" : right.toString();
        return result;

    }

    public void draw(double x, double y, boolean textLeft) {
        StdDraw.filledCircle(x, y, 0.02);
        if (textLeft) {
            StdDraw.textLeft(x + 0.05, y, key + " (" + cs.getName() + ")");
        } else {
            StdDraw.textRight(x - 0.05, y, key + " (" + cs.getName() + ")");
        }


        if (left != null) {
            StdDraw.line(x, y, x - 0.2, y - 0.1);
            left.draw(x - 0.2, y - 0.1, false);
        }
        if (right != null) {
            StdDraw.line(x, y, x + 0.2, y - 0.1);
            right.draw(x + 0.2, y - 0.1, true);
        }
    }

}


