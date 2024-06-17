import java.awt.*;
import java.util.Scanner;
import java.util.Random;

public class Simulation {

    // one astronomical unit (AU) is the average distance of earth to the sun.
    public static final double AU = 150.0e9;
    public static double Window = 2.0 * AU;
    public static boolean mods = true; // if false, simulation similar to real time travel of bodies meaning slow movement

    // all quantities are based on units of kilogram respectively second and meter.
    // The main simulation method using instances of other classes.
    public static void main(String[] args) {
        Body sun = new Body("Sol", 1.989e32, 696340e3, new Vector3(0, 0, 0), new Vector3(0, 0, 0), StdDraw.YELLOW);
        Body venus = new Body("Venus", 4.86747e24, 6052e3, new Vector3(-1.394555e11, 5.103346e10, 0), new Vector3(-103080.53, -281690.38, 0), StdDraw.PINK);
        Body mercury = new Body("Mercury", 6.41712e23, 3390e3, new Vector3(-1.010178e11, -2.043939e11, 0), new Vector3(206510.98, -101860.67, -23020.79), StdDraw.GRAY);
        Body earth = new Body("Earth", 5.972e24, 6371e3, new Vector3(-1.707667e10, 1.066132e11, 2.450232e9), new Vector3(-344460.02, -55670.47, 21810.10), StdDraw.BOOK_BLUE);
        Body mars = new Body("Mars", 3.301e23, 2440e3, new Vector3(-5.439054e10, 9.394878e9, -1.591727E9), new Vector3(-171170.83, -462970.48, -19250.57), StdDraw.RED);

        //manual inputs
        int n = 5;
        double T = 1; // set Theta for force Approximiation.
        Body[] bodies = new Body[]{sun, earth, mercury, venus, mars};
        boolean drawOcts = false; // if true, draws octants

        // Scan input and create random bodies and insert them into an Array
        /*
        int n = scanNumberOfBodies();
        double T = scanTheta();
        Body[] bodies = initializeBodies(n);
        //Body[] bodies = readBodies(n);
        boolean drawOcts = scanDraw(); // boolean if Octants should be drawn
         */


        if (bodies == null) {
            System.out.println("No Bodies created, ending Simulation.");
            System.exit(0);
        }


        System.out.println("Input was successful.\nINITIALIZING SIMULATION............");

        StdDraw.setCanvasSize(720, 720);
        StdDraw.setXscale(-(Window), Window);
        StdDraw.setYscale(-Window, Window);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);

        int seconds = 0;
        // simulation loop

        while (true) {
            // building the tree
            Tree octTree = new Tree();
            octTree.setTheta(T);
            for (int i = 0; i < n; i++) {
                octTree.add(bodies[i]);
            }
            // Calculate the total octMass and massCenter of each Subtree
            octTree.finishTree();
            // if (seconds == 0) System.out.println("Initial number of Bodies" + octTree.numberOfBodies());

            // for each body: move it according to the total force exerted on it using Barnes Hut Algo.
            for (Body body: bodies) {
                body.setForce(octTree.calcForce(body));
            }
            for (Body body: bodies) {
                body.move();
            }
            // clear old positions (exclude the following line if you want to draw orbits).
            StdDraw.clear(StdDraw.BLACK);

            // draw new positions, need to be updated to reflect z-position for the order of drawing
            for (int i = 0; i < n; i++) bodies[i].draw();

            if (drawOcts) octTree.drawOctant();
            //System.out.println(octTree.drawOctant()); // debug, print number of drawn octants

            // show new positions
            StdDraw.show();
            seconds++;
        }

    }


    //
    // Methods for creating bodies and putting them into an Array

    /**
     Method for creating random bodies. The first Body is always the Body Sol with the highest Mass, to represent the Center

     @param n The Amount of bodies to be created.
     @return An Array filled with n bodies.
     */
    private static Body[] initializeBodies(int n) {
        if (n <= 0) return null;
        Body[] bodies = new Body[n];
        bodies[0] = new Body("Sol", 1.989e32, 696340e3, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Color(255, 204, 51));
        for (int i = 1; i < n; i++) {
            String name = nameBuilder();
            double radius = radiusRandom();
            double mass = massRandom();
            Vector3 position = new Vector3(posDoubleRandom(), posDoubleRandom(), posDoubleRandom());
            Vector3 movement = new Vector3(movDoubleRandom(), movDoubleRandom(), movDoubleRandom());
            Color color = new Color(decRGBColor(), decRGBColor(), decRGBColor());
            if (mass > 1e28) {
                movement = movement.times(0);
                position = position.times(2.0);
            }
            bodies[i] = new Body(name, mass, radius, position, movement, color);
        }
        return bodies;
    }

    /**
     @return Random generated Vector3, with numbers fit for the position of a Body
     Variable out because of Floating Point Calculations.
     */
    private static double posDoubleRandom() {
        Random rng = new Random();
        double gaus = rng.nextGaussian() * 0.2;
        return (Window * gaus) * Math.pow(-1, (int) (Math.random() * 2));
    }

    /**
     @return Random generated Vector3, with numbers fit for movement of a Body
     */
    private static double movDoubleRandom() {
        double x = Math.random() + 10.0;
        while (x < 5.0) {
            x = Math.random() * 10.0;
        }
        return (Math.pow(-1.0, (int) (Math.random() * 2.0))) *
                (x * Math.pow(10.0, 3 + (Math.random() * 1.5)));

    }

    /**
     @return Random generated double, with numbers fit for mass of a Body ([1.0, 10[) * (10 ^[20, 30]) kg
     */
    private static double massRandom() {
        int x = (int) (Math.random() * 1000);
        if (x % 229 == 0) return 2.5e29 + 1e31 * Math.random();
        return 4.2e24 * Math.random();
    }

    private static double radiusRandom() {
        return (Math.random() * 1000 + Math.random() * 10000.0) * (Math.pow(10.0, 3));
    }

    private static String nameBuilder() {
        String lib = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder name = new StringBuilder();
        int length = 3 + (int) (Math.random() * 6); // min. 3 Characters, max 8
        for (int i = 0; i < length; i++) {
            name.append(lib.charAt((int)
                    (Math.random() * lib.length())
            ));
        }
        return name.toString();
    }

    /**
     @return Generates a Integer from 100 to 254
     */
    private static int decRGBColor() {
        return 100 + (int) (Math.random() * 155);
    }

    /**
     @return Scans Console input and returns positive Integer, hopefully foolproof...
     */
    private static int scanNumberOfBodies() {
        System.out.println("Please Enter the requested number of bodies as an positive Integer");
        Scanner input = new Scanner(System.in);
        int n;
        while (!input.hasNextInt()) {
            System.out.println("Not an valid Integer, try again: ");
            input = new Scanner(System.in);
        }
        n = input.nextInt();
        if (n < 1) n = scanNumberOfBodies();
        return n;
    }

    /**
     Scan for Theta until a valid Double has been entered.

     @return
     */
    private static double scanTheta() {
        System.out.println("Please Enter the requested Theta T as a positive Double. Suggestions is T = 1");
        Scanner input = new Scanner(System.in);
        double n;
        while (!input.hasNextDouble()) {
            System.out.println("Not a valid Theta, maybe wrong decimal separator. Try again: ");
            input = new Scanner(System.in);
        }
        n = input.nextDouble() * 1.0;
        if (n < 0) n = scanNumberOfBodies();
        return n;
    }

    /**
     Scan until char 'y' or 'n' has been entered for drawing octants or not

     @return boolean
     */
    private static boolean scanDraw() {
        System.out.println("Please Enter if the Octant should be drawn. y/n");
        Scanner input = new Scanner(System.in);
        char x = input.next().charAt(0);
        while (x != 'y' && x != 'n') {
            System.out.println("Please Enter if the Octants should be drawn. y/n");
            input = new Scanner(System.in);
            x = input.next().charAt(0);
        }
        if (x == 'y') return true;
        else return false;
    }
}