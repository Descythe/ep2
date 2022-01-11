import java.awt.*;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class CelestialBody implements Cluster {
    //TODO: change modifiers.
    private String name;
    private double mass;
    private double radius;
    private Vector3 position; // position of the center.
    private Vector3 currentMovement;
    private Color color; // for drawing the body.
    private Vector3 currentForce = new Vector3(0,0,0);

    //TODO: define constructor.

    public CelestialBody(String name, double mass, double radius, Vector3 position,
                         Vector3 currentMovement, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.currentMovement = currentMovement;
        this.color = color;
    }

    public CelestialBody(CelestialBody body, Vector3 position, Vector3 currentMovement) {
        this(body.name, body.mass, body.radius, position, currentMovement, body.color);
    }

    // Returns the distanceTo between this celestial body and the specified 'body'.
    public double distanceTo(CelestialBody body) {

        //TODO: implement method.
        return this.position.distanceTo(body.position);
    }

    // Returns a vector representing the gravitational force exerted by 'body' on this celestial body.
    public Vector3 gravitationalForce(CelestialBody body) {

        //TODO: implement method.
        Vector3 direction = body.position.minus(this.position);
        double r = direction.length();
        direction.normalize();
        double force = Simulation.G*this.mass*body.mass/(r*r);
        return direction.times(force);
    }

    // Moves this body to a new position, according to the specified force vector 'force' exerted
    // on it, and updates the current movement accordingly.
    // (Movement depends on the mass of this body, its current movement and the exerted force.)
    public void move(Vector3 force) {

        //TODO: implement method.
        Vector3 oldPosition = this.position;
        Vector3 newPosition = this.position.plus(this.currentMovement).plus(force.times(1/mass));
        currentMovement = newPosition.minus(oldPosition);
        position = newPosition;
    }

    // the following two methods could be replaced after refactoring
    public void move() {

        move(currentForce);
        currentForce = new Vector3(0,0,0);
    }

    public void addForce(Vector3 addForce) {

        currentForce = currentForce.plus(addForce);
    }

    // Returns the name of the body.
    public String toString() {

        //TODO: implement method (changed for AB5)
        return name;
    }

    // Returns a string with the information about this celestial body including
    // name, mass, radius, position and current movement. Example:
    // "Earth, 5.972E24 kg, radius: 6371000.0 m, position: [1.48E11,0.0,0.0] m, movement: [0.0,29290.0,0.0] m/s."
    public String toStringDetailed() {

        return name + ", " + mass + " kg, radius: " + radius + " m, position: " + position.toString() +
                " m, " +
                "movement: " + currentMovement.toString() + " m/s.";
    }

    // Prints the information about this celestial body including
    // name, mass, radius, position and current movement, to the console (without newline).
    // Earth, 5.972E24 kg, radius: 6371000.0 m, position: [1.48E11,0.0,0.0] m, movement: [0.0,29290.0,0.0] m/s.
    public void print() {

        System.out.print(toString());
    }

    // Draws the celestial body to the current StdDraw canvas as a dot using 'color' of this body.
    // The radius of the dot is in relation to the radius of the celestial body
    // (use a conversion based on the logarithm as in 'Simulation.java').
    public void draw() {

        position.drawAsDot(1, color);
    }

    // Returns the name of this celestial body.
    public String getName() {

        return name;
    }

    @Override
    public double mass() {
        return mass;
    }

    @Override
    public Vector3 massCenter() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != CelestialBody.class) {
            return false;
        }

        CelestialBody b = (CelestialBody) o;

        return b.getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}

