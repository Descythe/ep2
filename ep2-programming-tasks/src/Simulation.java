import java.io.IOException;

public class Simulation {

    // gravitational constant
    public static final double G = 6.6743e-11;

    // one astronomical unit (AU) is the average distance of earth to the sun.
    public static final double AU = 150e9;

    // all quantities are based on units of kilogram respectively second and meter.

    // The main simulation method using instances of other classes.
    public static void main(String[] args) {

        //create bodies and initialize position and currentMovement values
        Body oumuamua = new Body("Oumuamua",8e6,0.2e3,new Vector3(0,0,0),new Vector3(0,0,0),
                StdDraw.YELLOW);
        Body sun = new Body("Sol",1.989e30,696340e3,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.YELLOW);
        Body earth = new Body("Earth",5.972e24,6371e3,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.BLUE);
        Body mercury = new Body("Mercury",3.301e23,2440e3,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.GRAY);
        Body venus = new Body("Venus",4.86747e24,6052e3,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.PINK);
        Body mars = new Body("Mars",6.41712e23,3390e3,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.RED);
        Body moon = new Body("Moon", 7.349e22, 1.737e6, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body deimos = new Body("Deimos", 1.8e20, 6e3, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body phobos = new Body("Phobos", 1.08e20, 5e3, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body vesta = new Body("Vesta", 2.5908e20, 5e5, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body pallas = new Body("Pallas", 2.14e20, 5e5, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body hygiea = new Body("Hygiea", 8.32e19, 5e5, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);
        Body ceres = new Body("Ceres", 9.394e20, 5e5, new Vector3(0,0,0), new Vector3(0,0,0),
                StdDraw.GRAY);

        ComplexCosmicSystem earthSystem = new ComplexCosmicSystem("Earth System", earth, moon);
        ComplexCosmicSystem marsSystem = new ComplexCosmicSystem("Mars System", mars, deimos,
                phobos);
        ComplexCosmicSystem solarSystem = new ComplexCosmicSystem("Solar System", sun, earthSystem,
                marsSystem);
        solarSystem.add(mercury);
        solarSystem.add(venus);
        solarSystem.add(vesta);
        solarSystem.add(pallas);
        solarSystem.add(hygiea);
        solarSystem.add(ceres);

        ComplexCosmicSystem milkyWay = new ComplexCosmicSystem("Milky Way",solarSystem, oumuamua);
        Vector3 forceOnBody;

        StdDraw.setCanvasSize(500, 500);
        StdDraw.setXscale(-4*AU,4*AU);
        StdDraw.setYscale(-4*AU,4*AU);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);

        System.out.println(milkyWay);
        if (args.length != 2) {
            System.out.println("Error: wrong number of arguments!");
        }

        BodyIterator iterator = milkyWay.iterator();

        do {
            Body b = iterator.next();
            if (b == sun) continue;
            try {
                boolean success = ReadDataUtil.readConfiguration(b, args[0] + "/" + b.getName()+
                                ".txt", args[1]);
                if (!success) {
                    System.out.println("Error: state not available!");
                    System.out.println("Running simulation without " + b.getName() + "...");
                    iterator.remove();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage() + "");
                System.out.println("Running simulation without " + b.getName() + "...");
                iterator.remove();
            }
            System.out.println(milkyWay);
        } while (iterator.hasNext());

        System.out.println("Running simulation ...");

        System.out.println(milkyWay);

        // Can use any of the following types ...
        //Body[] bodies = milkyWay.getBodies().toArray();
        //BodyCollection bodies = milkyWay.getBodies();
        ComplexCosmicSystem bodies = milkyWay;

        double seconds = 0;

         // simulation loop for 200 days
        while(seconds / (3600*24) < 200) {

            seconds++; // each iteration computes the movement of the celestial bodies within one second.

            // for each body (with index i): compute the total force exerted on it.
            //we now calculate the force for each body and assign it to it via setForce()
            for (Body b1: bodies) {
                forceOnBody = new Vector3(0,0,0); // begin with zero
                for (Body b2: bodies) {
                    if (b1 == b2) continue;
                    Vector3 forceToAdd = b1.gravitationalForce(b2);
                    forceOnBody = forceOnBody.plus(forceToAdd);
                }
                b1.setForce(forceOnBody);
            }

            // for each body (with index i): move it according to the total force exerted on it.
            for (Body b1: bodies) {
                b1.move();
            }

            // show all movements in StdDraw canvas only every 3 hours (to speed up the simulation)
            if (seconds%(3*3600) == 0) {
                //System.out.println(seconds);
                // clear old positions (exclude the following line if you want to draw orbits).
                StdDraw.clear(StdDraw.BLACK);

                // draw new positions
                for (Body b1: bodies) {
                    b1.draw();
                }

                // show new positions
                StdDraw.show();
            }

        }

    }
}
