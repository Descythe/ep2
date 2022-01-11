public interface Cluster {
    // returns the mass of this entire cluster.
    double mass();

    // return the mass massCenter of this cluster (massCenter of gravity).
    Vector3 massCenter();

    // draw the cluster.
    void draw();
}
