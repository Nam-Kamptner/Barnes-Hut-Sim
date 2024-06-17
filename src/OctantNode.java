import java.awt.*;

public class OctantNode {
    private OctantNode nodes[] = new OctantNode[8];

    private Body body;
    private final Vector3 center;
    private final double length;
    private Vector3 massCenter;
    private double octMass;

    public OctantNode(Body body, Vector3 center, double length) {
        this.body = body;
        this.center = center;
        this.length = length;
        massCenter = new Vector3(0, 0, 0);
        octMass = 0;
    }

    /**
     @param body tries to add body to the Tree. <p>Following possibilities:</p>
     <p>Case isLeaf_1: Body is null, adds body to this Node.</p>
     <p>Case isLeaf_2: Body is occupied, creates 8 Nodes (the Octants of this Node) and (re-) adds the old and new Body
     to one of the new Octants.</p>
     <p>Case Not IsLeaf: searches the corresbonding Octant, the body would fit in.
     <p>
     Methods used: {@link #createOcts()}
     <p>
     */
    public void add(Body body) {
        if (this.body == null && isLeaf()) {
            this.body = body;
            return;
        } else if (isLeaf()) {
            createOcts();
            this.add(this.body);
            this.body = null;

        }
        boolean found = false;
        for (int i = 0; i < 8; ++i) {
            if (!found) {
                if (nodes[i].enclose(body, nodes[i].length, nodes[i].center)) {
                    nodes[i].add(body);
                    found = true;
                }
            } else return;
        }
    }


    /**
     @return Boolean. If this Node has no Childs, then Node is a Leaf Node.
     */
    private boolean isLeaf() {

        for(int i =0;i<8;++i)
        {
            if(nodes[i]!=null)
            {
                return false;
            }
        }
        return true;
    }

    /**
     @param body      the body to be checked.
     @param l         length of Octant
     @param octCenter Center (x,y,z) of the Node
     @return Boolean. Checks if the Body is within the Nodes Area by calculating the outer boundaries and verifies if
     Body is within the boundary.
     */
    public boolean enclose(Body body, double l, Vector3 octCenter) {
        double half = l * 0.5;
        Vector3 centerPlusHalf = octCenter.plus(new Vector3(half, half, half));
        Vector3 centerMinusHalf = octCenter.minus(new Vector3(half, half, half));
        return body.within(centerPlusHalf, centerMinusHalf);
    }

    /**
     Divides the current Node Area (x,y,z) into 8 subdivision with the same size, representing the 8 Octants.
     */
    private void createOcts() {
        double octLength = length * 0.25;
        double l = length * 0.5;

        nodes[0] = new OctantNode(null, center.plus(new Vector3(octLength, octLength, octLength)), l);
        nodes[1] = new OctantNode(null, center.plus(new Vector3(-octLength, octLength, octLength)), l);
        nodes[2] = new OctantNode(null, center.plus(new Vector3(octLength, -octLength, octLength)), l);
        nodes[3] = new OctantNode(null, center.plus(new Vector3(-octLength, -octLength, octLength)), l);

        nodes[4] = new OctantNode(null, center.plus(new Vector3(octLength, octLength, -octLength)), l);
        nodes[5] = new OctantNode(null, center.plus(new Vector3(-octLength, octLength, -octLength)), l);
        nodes[6] = new OctantNode(null, center.plus(new Vector3(octLength, -octLength, -octLength)), l);
        nodes[7] = new OctantNode(null, center.plus(new Vector3(-octLength, -octLength, -octLength)), l);
    }

    /**
     @param b The Force impacting this body by other Plantes/Clusters will be calculated.
     @param T The Theta Threshold, determining if a Octant is far enough from the Body b.
     @return As soon as a Octant is far enough (approximated by [(d / r) < T]) from the body:
     <p>Force impacting the Body from this Octant will be calculated.</p>
     <p>For the Calculation the Octants totalMass and the massCenter are used with a dummy Body representing the Octant
     and using {@link Body#gravitationalForce(Body)}</p>
     */

    public Vector3 forceEstimate(Body b, double T) {
        Vector3 out = new Vector3(0,0,0);
        if (isLeaf()) {
            if (body == null || b.equals(body)) return new Vector3(0,0,0);
            out = b.gravitationalForce(body);
        }
        else {
            Body octDummy = new Body("dummy", octMass, 5000e3, center, new Vector3(0, 0, 0), Color.WHITE);
            double r = octDummy.distanceTo(b);

            // d = lenth
            if ((length /r) < T) {
                octDummy = new Body("dummy", octMass, 5000e3, massCenter, new Vector3(0,0,0), Color.white);
                out = out.plus(b.gravitationalForce(octDummy));
            } else {
                for(int i =0;i<8;++i)
                {
                    out = out.plus(nodes[i].forceEstimate(b, T));
                }
            }
        }
        return out;
    }

    /**
     @return Calculates the total Mass for this Octant, with the sum of all Bodies Mass within the sub Tree.
     */
    public double setOctMass() {
        if (isLeaf()) {
            if (body != null) {
                this.octMass += body.getMass();
            }
        } else {
            for(int i =0;i<8;++i)
            {
                this.octMass += nodes[i].setOctMass();
            }
        }
        return this.octMass;
    }

    /**
     @return Calculates the Mass Center of Octant by summing up all Bodies positions within the Subtree multiplied by their mass,
     then dividing the Total by the totalMass of the Octant.
     */
    public Vector3 setMassCenter() {
        if (isLeaf()) {
            if (body == null) {
                return new Vector3(0, 0, 0);
            }
            massCenter = body.getMassCenter();
            return body.getMassCenter().times(octMass);
        } else {
            for(int i =0;i<8;++i)
            {
                massCenter = massCenter.plus(nodes[i].setMassCenter());
            }
}
        massCenter = massCenter.times(1.0/ octMass);
        return massCenter.times(octMass);
    }

    /**
     @return Number of Bodies stored inside the Tree
     */
    public int numberOfBodies() {
        int out = 0;
        if (body != null) out++;
        if (!isLeaf()) {
            for(int i =0;i<8;++i)
            {
                out += nodes[i].numberOfBodies();
            }
        }
        return out;
    }

    public String toString() {
        String out = "";
        if (body != null) {
            out += this.body.getName() + ", ";
        }
        if (!isLeaf()) {
            for(int i =0;i<8;++i)
            {
                out += nodes[i].toString();
            }
        }
        return out;
    }

    /**
     Draws Octants in Areas where there is a body stored using
     {@link Vector3#drawOct(double, Color)}  Center is center of Node, passing length of Node
     and color of Body as parameters
     @return
     */
    public int drawOct() {
        int counter = 0;
        if (body != null) {
            counter++;
            center.drawOct(length, body.getColor());
        } else if (!isLeaf()) {
            for(int i =0;i<8;++i)
            {
                counter += nodes[i].drawOct();
            }
        }
        return counter;
    }

    public int getMaxHeight() {
        int maxHeight = 0;
        boolean hasChildren = false;
        for (int i = 0; i < 8; i++) {
            if (nodes[i] != null) hasChildren = true;
        }
        if (!hasChildren) {
            return maxHeight;
        }
            int partHeight;
            for (int i = 0; i < 8; i++) {
                partHeight = nodes[i].getMaxHeight() + 1;
                if (partHeight > maxHeight) maxHeight = partHeight;

        }
        return maxHeight;
    }

    /*
    for(int i =0;i<8;++i)
            {
                this.octMass += nodes[i].setOctMass();
            }
     */
  /*  public int getNumberOfBodies(int level) {
        int maxNum = 0;
        if (level == 1) {
            for(int i = 0;i<8;++i)
            {
                if (isLeaf()) {
                    if (nodes[i].body != null) maxNum++;
                }
            }
        } else {
            for(int i =0;i<8;++i)
            {
                if (!nodes[i].isLeaf())
                maxNum += nodes[i].getNumberOfBodies(level - 1);
            }
        }
        return maxNum;
    }*/
    public int numberOfBodiesAtLevel(int level)
    {
        int count = 0;
        if(level == 1)
        {
            for(int i =0;i<8;++i)
            {
                if(nodes[i].isLeaf() && nodes[i].body!=null)
                {
                    ++count;
                }
            }
        }
        else
        {
            for(int i =0;i<8;++i)
            {
                if(!nodes[i].isLeaf())
                {
                    count += nodes[i].numberOfBodiesAtLevel(--level);
                }
            }
        }
        return count;
    }

}