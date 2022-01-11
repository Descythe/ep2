import java.util.Iterator;
import java.util.NoSuchElementException;

public class OcTree implements Iterable<CelestialBody> {
    OcTreeNode root = new OcTreeEmptyNode(new Octant(new Vector3(Simulation.OCT_MIN,Simulation.OCT_MIN,Simulation.OCT_MIN),
            new Vector3(Simulation.OCT_MAX, Simulation.OCT_MAX, Simulation.OCT_MAX)));

    public void add(CelestialBody s) {
        if (s.massCenter().getX() < Simulation.OCT_MIN || s.massCenter().getX() > Simulation.OCT_MAX
                || s.massCenter().getY() < Simulation.OCT_MIN || s.massCenter().getY() > Simulation.OCT_MAX
                || s.massCenter().getZ() < Simulation.OCT_MIN || s.massCenter().getZ() > Simulation.OCT_MAX) {
            return;
        }
        root = root.add(s);
    }

    public void draw() {
        root.draw();
    }

    public Iterator<CelestialBody> iterator() {
        return root.iterator();
    }

    public void addForceTo(CelestialBody s) {
        root.addForceTo(s);
    }
}

interface OcTreeIterable extends Iterable<CelestialBody> {
    OcTreeIterator iterator();
}

interface OcTreeIterator extends Iterator<CelestialBody> {
    boolean hasNext();
    CelestialBody next();
}

interface OcTreeNode extends Cluster, OcTreeIterable {

    OcTreeNode add(CelestialBody s);
    void draw();
    Vector3 massCenter();
    double mass();
    void addForceTo(CelestialBody s);
}

class OcTreeClusterNode implements OcTreeNode, Iterable<CelestialBody> {

    private Octant area;
    private OcTreeNode[] subnodes;
    private double mass;
    private Vector3 massCenter;

    public OcTreeClusterNode(Octant octant) {
        this.area = octant;

        Octant[] subOctants = octant.subOctants();
        this.subnodes = new OcTreeNode[8];
        for(int i = 0; i < 8; i++) {
            this.subnodes[i] = new OcTreeEmptyNode(subOctants[i]);
        }
        this.mass = 0;
        this.massCenter = null;
    }

    public OcTreeNode add(CelestialBody s) {

        massCenter = massCenter == null ?
                s.massCenter() :
                s.massCenter().plus(massCenter.times(-1)).times(s.mass()/(s.mass()+mass)).plus(massCenter);
        mass += s.mass();
        int index = area.subOctantIndex(s.massCenter());
        subnodes[index] = subnodes[index].add(s);
        return this;
    }

    public void draw() {
        for(int i = 0; i < 8; i++) {
            subnodes[i].draw();
        }
    }

    public double mass() {
        return this.mass;
    }
    public Vector3 massCenter() {
        return this.massCenter;
    }

    public void addForceTo(CelestialBody s) {
        double r = this.massCenter.distanceTo(s.massCenter());
        double d = area.size();
        if (r/d > Simulation.ACC && this.area.subOctantIndex(s.massCenter()) < 0) {
            double force = Simulation.G*this.mass*s.mass()/(r*r*r);
            Vector3 direction = this.massCenter.plus(s.massCenter().times(-1));
            s.addForce(direction.times(force));
        } else {
            for(int i = 0; i < 8; i++) {
                subnodes[i].addForceTo(s);
            }
        }
    }

    public OcTreeIterator iterator() {
        return new OcTreeClusterNodeIterator();
    }

    class OcTreeClusterNodeIterator implements OcTreeIterator {

        private OcTreeIterator iter;
        private boolean hasNext;
        private int i = 0;

        public OcTreeClusterNodeIterator() {
            iter = subnodes[i].iterator();
            while (i < 7 && !iter.hasNext()) {
                iter = subnodes[++i].iterator();
            }

            hasNext = iter.hasNext();
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public CelestialBody next() {
            if (!hasNext) throw new NoSuchElementException();

            CelestialBody s = iter.next();

            while (i < 7 && !iter.hasNext()) {
                iter = subnodes[++i].iterator();
            }

            hasNext = iter.hasNext();

            return s;

        }

    }
}

class OcTreeLeafNode implements OcTreeNode {

    private CelestialBody s;
    private Octant area;

    public OcTreeLeafNode(CelestialBody s, Octant octant) {
        this.s = s;
        this.area = octant;
    }

    public OcTreeNode add(CelestialBody s) {
        OcTreeClusterNode result = new OcTreeClusterNode(area);
        result.add(this.s);
        result.add(s);
        return result;
    }

    public void draw() {
        s.draw();
        area.draw();
    }

    public double mass() {
        return s.mass();
    }

    public Vector3 massCenter() {
        return s.massCenter();
    }

    public void addForceTo(CelestialBody s) {
        if (this.s == s) {
            return;
        }
        Vector3 massCenter = this.massCenter();
        double r = massCenter.distanceTo(s.massCenter());

        double force = Simulation.G*this.mass()*s.mass()/(r*r*r);
        Vector3 direction = massCenter.plus(s.massCenter().times(-1));
        s.addForce(direction.times(force));

    }

    public OcTreeIterator iterator() {
        return new OcTreeLeafNodeIterator();
    }

    class OcTreeLeafNodeIterator implements OcTreeIterator {

        private boolean hasNext;

        public OcTreeLeafNodeIterator() {
            hasNext = true;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public CelestialBody next() {
            if (!hasNext) throw new NoSuchElementException();

            hasNext = false;

            return s;

        }

    }
}

class OcTreeEmptyNode implements OcTreeNode {

    private Octant area;

    public OcTreeEmptyNode(Octant octant) {
        this.area = octant;
    }

    public OcTreeNode add(CelestialBody s) {

        return new OcTreeLeafNode(s, area);
    }

    public void draw() {
        // do nothing
    }

    public double mass() {
        return 0d;
    }

    public Vector3 massCenter() {
        return null;
    }

    public void addForceTo(CelestialBody s) {
        // do nothing
    }

    public OcTreeIterator iterator() {
        return new OcTreeIterator() {
            @Override
            public boolean hasNext() {
                return false;
            }
            public CelestialBody next() {
                throw new NoSuchElementException();
            }
        };
    }
}

class Octant {
    private Vector3 min;
    private Vector3 max;

    private Vector3 center;

    public Octant(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;

        this.center = max.plus(min).times(0.5);
    }

    public int subOctantIndex(Vector3 point) {
        if (point.getX() < this.min.getX() || point.getX() > this.max.getX()
        || point.getY() < this.min.getY() || point.getY() > this.max.getY()
        || point.getZ() < this.min.getZ() || point.getZ() > this.max.getZ()) {
            return -1;
        }
        int index = this.center.getX() < point.getX() ? 1 : 0;
        index += this.center.getY() < point.getY() ? 2 : 0;
        index += this.center.getZ() < point.getZ() ? 4 : 0;

        return index;
    }

    public Octant[] subOctants() {
        Octant[] result = new Octant[8];
        for (int i = 0; i < 8; i++) {
            result[i] = subOctant(i);
        }
        return result;
    }

    public void draw() {
        // do nothing
    }

    public Octant subOctant(int index) {

        double maxX, maxY, maxZ, minX, minY, minZ;

        if ((index & 1) == 1) {
            minX = center.getX();
            maxX = max.getX();
        } else {
            minX = min.getX();
            maxX =  center.getX();
        }

        if ((index & 2) == 2) {
            minY = center.getY();
            maxY = max.getY();
        } else {
            minY = min.getY();
            maxY =  center.getY();
        }

        if ((index & 4) == 4) {
            minZ = center.getZ();
            maxZ = max.getZ();
        } else {
            minZ = min.getZ();
            maxZ =  center.getZ();
        }

        return new Octant(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));

    }

    public double size() {
        return max.getX() - min.getX();
    }

}
