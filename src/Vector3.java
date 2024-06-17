import java.awt.*;

// This class represents vectors in a 3D vector space.
public class Vector3 {

    //private modifiers
    private double x;
    private double y;
    private double z;

    //Constructor
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Returns the sum of this vector and vector 'v'.
    public Vector3 plus(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    // Returns the product of this vector and 'd'.
    public Vector3 times(double d) {
        return new Vector3(x * d, y * d, z * d);
    }

    // Returns the sum of this vector and -1*v.
    public Vector3 minus(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    // Returns the Euclidean distanceTo of this vector
    // to the specified vector 'v'.
    public double distanceTo(Vector3 v) {
        double dX = x-v.x;
        double dY = y-v.y;
        double dZ = z-v.z;

        return Math.sqrt(dX*dX+dY*dY+dZ*dZ);
    }

    // Returns the length (norm) of this vector.
    public double length() {
        return distanceTo(new Vector3(0, 0, 0));
    }

    // Normalizes this vector: changes the length of this vector such that it becomes 1.
    // The direction and orientation of the vector is not affected.
    public void normalize() {
        double length = length();
        if (length == 0) length = 1.0;
        x /= length;
        y /= length;
        z /= length;
    }

    // Draws a filled circle with a specified radius centered at the (x,y) coordinates of this vector
    // in the existing StdDraw canvas. The z-coordinate is not used.
    public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(x, y, radius);
    }

    /**

     @param v
     @return vector3 v coordinates are all smaller or equal to this coordinates
     */
    public boolean isSmaller(Vector3 v) {
        return (x <= v.x && y <= v.y && z <= v.z);
    }

    /**

     @param length length of Octant
     @param c Color for the Square
     */
    public void drawOct(double length, Color c) {
        StdDraw.setPenColor(c);
        StdDraw.square(x, y, length / 2.0);
    }

    // Returns the coordinates of this vector in brackets as a string
    // in the form "[x,y,z]", e.g., "[1.48E11,0.0,0.0]".
    public String toString() {
        return "[x: " + String.format("%.3e", x) + ", y: " + String.format("%6.3e", y) + ", z: ]";
    }
}
