import java.awt.*;
import java.util.*;
import java.util.List;


public class Simulation {

    // all quantities are based on units of kilogram respectively second and meter.

    // gravitational constant
    public static final double G = 6.6743e-11;

    // one astronomical unit (AU) is the average distanceTo of earth to the sun.
    public static final double AU = 150e9;

    // one light year
    public static final double LY = 9.461e15;

    // set octant size
    public static final double OCT_MIN = -0.01*LY;
    public static final double OCT_MAX = 0.01*LY;

    public static final double ACC = 1; // octree threshold for approximation

    public static void main(String[] args) {

        StdDraw.setCanvasSize(500, 500);
        StdDraw.setXscale(OCT_MIN,OCT_MAX);
        StdDraw.setYscale(OCT_MIN,OCT_MAX);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);

        // some variables for positioning of stars:
        // in order to have visible movements we place many massive stars in a very small region.
        double initialSpeed = 0.000001*LY;
        double clusterWidth = 0.0001*LY;
        double offset = 0.0006*LY;

        Random random = new Random(1000);

        // starcluster A
        List<CelestialBody> celestialBodies = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            celestialBodies.add(new CelestialBody("A"+i, random.nextGaussian()*2e30,1,
                    new Vector3(random.nextGaussian()*clusterWidth-offset,
                            random.nextGaussian()*clusterWidth-offset,
                            random.nextGaussian()*clusterWidth-offset),
                    new Vector3(random.nextGaussian()*initialSpeed+10*initialSpeed,random.nextGaussian()*initialSpeed,
                            random.nextGaussian()*initialSpeed),
                    new Color(r,g,b)) );
        }


        // starcluster B
        for (int i = 0; i < 5000; i++) {
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            celestialBodies.add(new CelestialBody("B"+i, random.nextGaussian()*2e30,1,
                    new Vector3(random.nextGaussian()*clusterWidth+offset,
                            random.nextGaussian()*clusterWidth+offset,
                            random.nextGaussian()*clusterWidth+offset),
                    new Vector3(random.nextGaussian()*initialSpeed-10*initialSpeed,random.nextGaussian()*initialSpeed,
                            random.nextGaussian()*initialSpeed),
                    new Color(r,g,b)) );
        }

        // one more heavy object in the middle
        celestialBodies.add(new CelestialBody("super massive black hole", 1e45, 3, new Vector3(0
                ,0,0),
                new Vector3(0,0,
                0), new Color(200,0,0)) );


        // initialize octree
        OcTree tree = new OcTree();
        for (CelestialBody celestialBody : celestialBodies) {
            tree.add(celestialBody);
        }

        // simulation loop
        while(true) {
            StdDraw.clear(StdDraw.BLACK);
            tree.draw();
            StdDraw.show();

            OcTree updated = new OcTree();
            for (CelestialBody celestialBody : tree) {
                tree.addForceTo(celestialBody);
            }

            for (CelestialBody celestialBody : tree) {
                celestialBody.move();
                updated.add(celestialBody);
            }
            tree = updated;
        }
    }
}
