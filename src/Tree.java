public class Tree {

    private final Vector3 UniverseCenter = new Vector3(0, 0, 0);
    private OctantNode root;
    private double Theta;

    /**
     @param body body that should be added to the tree.
     */
    public void add(Body body) {
        if (body == null) {
            return;
        }
        if (root == null) {
            root = new OctantNode(body, UniverseCenter, 2.0 * Simulation.Window);
        } else {
            root.add(body);
        }
    }

    public void finishTree() {
        if (root == null) return;
        root.setOctMass();
        root.setMassCenter();
    }

    public void setTheta(double T) { Theta = T; }

    /**
     @param b The Force impacting this body by other Plantes/Clusters will be calculated.
     @return Sum of the forces of Octants far enough using {@link OctantNode#forceEstimate(Body, double)}
     */
    public Vector3 calcForce(Body b) {
        if (root == null) return new Vector3(0, 0, 0);
        return root.forceEstimate(b, Theta);
    }

    public void drawOctant() {
        if (root == null) return;
        root.drawOct();
    }

    public String toString() {
        if (root != null) return root.toString();
        else return "Empty";
    }

    public int numberOfBodies() {
        if (root != null) return root.numberOfBodies();
        else return 0;
    }

    public int getHeight() {
        if (root == null) return 0;
         else return root.getMaxHeight() + 1;
    }

    public int getNumberOfBodiesAtLevel(int level) {
        if (root == null) return 0;
        if (level <= 0) {
            return 0;
        }
        return root.numberOfBodiesAtLevel(level);
    }

}
