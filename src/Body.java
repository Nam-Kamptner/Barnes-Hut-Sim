import java.awt.*;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class Body {

    public static final double G = 6.6743e-11;

    //private modifiers
    private final String name;
    private final double mass;
    private final double radius;
    private final Color color; // for drawing the body.
    private Vector3 position; // position of the center.
    private Vector3 currentMovement;
    private Vector3 force;

    // Constructor
    public Body(String name, double mass, double radius, Vector3 position, Vector3 currentMovement, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.currentMovement = currentMovement;
        this.color = color;
        force = new Vector3(0, 0, 0);
    }

    // Returns the distanceTo between this body and the specified 'body'.
    public double distanceTo(Body body) {
        return position.distanceTo(body.position);
    }

    /**
     @param body body that exerts force on this body.
     @return Returns a vector representing the gravitational force exerted by 'body' on this body.
     <p>The gravitational Force F is calculated by F = G*(m1*m2)/(r*r), with m1 and m2 being the masses of the objects</p>
     <p>interacting, r being the distanceTo between the centers of the masses and G being the gravitational constant.</p>
     <p>To calculate the force exerted on b1, simply multiply the normalized vector pointing from b1 to b2 with the
     calculated force</p>
     */
    public Vector3 gravitationalForce(Body body) {
        double phi = 0; // to reduce intense Force because Bodies are too close than should be in Reality
        if (Simulation.mods) phi = 7e9;
        Vector3 direction = body.position.minus(position);
        double r = direction.length();
        direction.normalize();
        double force = (G * mass * body.mass) / (r * r + phi * phi);
        return direction.times(force);
    }

    // Moves this body to a new position, according to the specified force vector 'force' exerted
    // on it, and updates the current movement accordingly.
    // (Movement depends on the mass of this body, its current movement and the exerted force)
    // Hint: see simulation loop in Simulation.java to find out how this is done
    public void move() {
        double mod = 10.0;
        if (Simulation.mods) mod = 3e3; // to speed up Movement, Movement in general stays the same, just faster
        currentMovement = currentMovement.plus(force.times(mod / mass));
        position = position.plus(currentMovement.times(mod));
    }

    // Returns a string with the information about this body including
    // name, mass, radius, position and current movement. Example:
    // "Earth, 5.972E24 kg, radius: 6371000.0 m, position: [1.48E11,0.0,0.0] m, movement: [0.0,29290.0,0.0] m/s."
  /*  public String toString() {
        return '"' + name + ", " + mass + " kg, radius: " + radius + " m, position: " + position.toString() +
                " m, movement: " + currentMovement.toString() + " m/s." + '"';
    }*/

    // Debugger to String, replace with above to use original toString
    public String toString() { return name + " - POS: " + position.toString() + " - MOV: " + currentMovement; }

    // Draws the body to the current StdDraw canvas as a dot using 'color' of this body.
    // The radius of the dot is in relation to the radius of the celestial body
    // (use a conversion based on the logarithm as in 'Simulation.java').
    // Hint: use the method drawAsDot implemented in Vector3 for this
    public void draw() {
        double r;
        if (mass > 1.0e28) {
            r = 2.5e9 * Math.log10(radius);
        } else {
            r = 9e8 * Math.log10(radius);
        }
        position.drawAsDot(r, color);
    }

    public String getName() { return name; }

    /*
    Schreiben sie die Klasse Body so um, sodass sie die aktuelle Kraft, die auf den Himmelskörper wirkt, als Objektvariable speichert.
    Diese Kraft soll weiterhin in Simulation berechnet werden und dann an das Objekt übergeben werden (z.B. setForce()).
    Zum Bewegen des Himmelskörpers implementieren Sie eine überladene Methode move() (ohne Parameter),
    die das Objekt gemäß der gespeicherten Kraft bewegt. Bauen Sie anschließend Simulation so um,
    sodass diese neuen Methoden genutzt werden, und kein Array von Kräften (Vector3[] forceOnBody) mehr benötigt wird.
     */
    public void setForce(Vector3 force) { this.force = force; }

    // returns if this Body is within the Boundaris of centerPlusHalf and centerMinudHalf
    public boolean within(Vector3 centerPlusHalf, Vector3 centerMinusHalf) {
        return (position.isSmaller(centerPlusHalf) && centerMinusHalf.isSmaller(position));
    }

    public double getMass() { return mass; }

    public Vector3 getMassCenter() { return position; }

    public Color getColor() {return color; }
}
