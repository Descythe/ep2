import java.awt.*;
import java.util.Objects;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class Body implements CosmicComponent {

    public static final double G = 6.6743e-11;

    private final String name;
    private final double mass;
    private final double radius;
    private Vector3 position; // position of the center.
    private Vector3 currentMovement;
    private Vector3 currentForce = new Vector3(0, 0, 0);
    private final Color color; // for drawing the body.

    public Body(String name, double mass, double radius, Vector3 position, Vector3 currentMovement, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.currentMovement = currentMovement;
        this.color = color;
    }

    public Body(Body body, Vector3 position, Vector3 currentMovement) {
        this(body.name, body.mass, body.radius, position, currentMovement, body.color);
    }

    // Returns the name of this celestial body.
    public String getName() {
        return name;
    }

    public int numberOfBodies() {
        return 1;
    }

    public double getMass() {
        return mass;
    }

    public Vector3 getMassCenter() {
        return position;
    }

    // Returns the distance between this celestial body and the specified 'body'.
    public double distanceTo(Body body) {

        return body.position.distanceTo(position);
    }

    // Returns a vector representing the gravitational force exerted by 'body' on this celestial body.
    public Vector3 gravitationalForce(Body body) {

        Vector3 direction = body.position.minus(position);
        double r = direction.length();
        direction.normalize();
        double force = G * mass * body.mass / (r * r);
        return direction.times(force);
    }

    public void setForce(Vector3 currentForce) {
        this.currentForce = currentForce;
    }

    public void move() {
        // Moves this body to a new position, according to the current force exerted
        // on it, and updates the current movement accordingly.
        move(currentForce);
    }

    // Moves this body to a new position, according to the specified force vector 'force' exerted
    // on it, and updates the current movement accordingly.
    // (Movement depends on the mass of this body, its current movement and the exerted force.)
    public void move(Vector3 force) {
        currentMovement = currentMovement.plus(force.times(1 / mass));
        position = position.plus(currentMovement);
    }

    // Returns a string with the information about this celestial body including
    // name, mass, radius, position and current movement. Example:
    // "Earth, 5.972E24 kg, radius: 6371000.0 m, position: [1.48E11,0.0,0.0] m, movement: [0.0,29290.0,0.0] m/s."
    @Override
    public String toString() {
        return name;
    }

    // Two 'Body' objects are equals if their names and their masses are equal.
    // Masses are compared with a tolerance. (Please note that the transitivity
    // condition of 'equals' specified in 'Object' is violated. We accept this
    // violation assuming that the mass difference between two different bodies
    // is relatively large).
    @Override
    public boolean equals(Object o) {
        double epsilon = 0.00000001;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Body body = (Body) o;
        return name.equals(body.name) && Math.abs(body.mass - this.mass) < epsilon;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    // Draws the body to the current StdDraw canvas as a dot using 'color' of this body.
    // The radius of the dot is in relation to the radius of the celestial body
    // (use a conversion based on the logarithm as in 'Simulation.java').
    public void draw() {
        // use log10 because of large variation of radii.
        position.drawAsDot(2e9 * Math.log10(radius), color);
        StdDraw.text(position.getX(), position.getY() - 30e9, name);
    }


    public void setState(Vector3 position, Vector3 currentMovement) {
        this.position = position;
        this.currentMovement = currentMovement;
    }
}

