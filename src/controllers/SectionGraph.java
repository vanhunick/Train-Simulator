package controllers;

import java.util.*;

/**
 * Created by User on 26/07/2016.
 */
public class SectionGraph {

    // The sections in the railway
    private ControllerSection[] sections;

    // Each node represents a section
    private Set<Node> nodes;


    // Node to use for finding shortest path
    private class Node implements Comparable<Node>{
        ControllerSection s;
        Node prev;
        double minCost = Double.POSITIVE_INFINITY;

        public Node(ControllerSection s){
            this.s = s;
        }

        @Override
        public int compareTo(Node o) {return Double.compare(minCost, o.minCost);}
    }


    public SectionGraph(ControllerSection[] sections){
        this.nodes = createNodes(sections);
        this.sections = sections;
    }


    public Node getNode(int sectionID){

        for(Node n : nodes){
            if(n.s.equals(sections[sectionID])){
                return n;
            }
        }
        System.out.println("No node");
        return null;
    }

    public Set<Node> createNodes(ControllerSection[] sections){
        Set nodes = new HashSet<>();

        for(ControllerSection s : sections){
            nodes.add(new Node(s));
        }

        return nodes;
    }

    public List<Integer> getRoute(ControllerSection start, ControllerSection destination, boolean nat){
        Node startNode = null;
        Node destNode = null;
        // Reset Nodes
        for(Node n : nodes) {
            if (n.s.equals(start)) {
                startNode = n;
            } else if (n.s.equals(destination)) {
                destNode = n;
            }
            n.minCost = Double.POSITIVE_INFINITY;
            n.prev = null;
        }

        startNode.minCost = 0;

        dijkstraSearch(startNode, nat);
        List<Integer> path = new ArrayList<>();
        getShortestPathTo(destNode).forEach(pn -> path.add(pn.s.id));

        return path;
    }

    /**
     * Computes the paths from the source
     * to all other nodes
     *
     * @param source to search from
     * */
    private void dijkstraSearch(Node source, boolean nat){
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>();
        nodeQueue.add(source);

        while (!nodeQueue.isEmpty()) {
            Node dn = nodeQueue.poll();
            Node juncDest = null;
            Node dest = null;

            // The train is going with the track
            if(dn.s.junctionIndex != -1){
                if(nat){
                    juncDest = getNode(dn.s.junctionIndex);

                    double destCost = juncDest.s.length;
                    double distanceThroughR = dn.minCost + destCost;
                    if (distanceThroughR < juncDest.minCost) {
                        nodeQueue.remove(juncDest);
                        juncDest.minCost = distanceThroughR;
                        juncDest.prev = dn;
                        nodeQueue.add(juncDest);
                    }
                }

                if(dn.s.containsJunction && nat && !dn.s.junction.inbound){
                    juncDest = getNode(dn.s.junctionIndex);

                    double destCost = juncDest.s.length;
                    double distanceThroughR = dn.minCost + destCost;
                    if (distanceThroughR < juncDest.minCost) {
                        nodeQueue.remove(juncDest);
                        juncDest.minCost = distanceThroughR;
                        juncDest.prev = dn;
                        nodeQueue.add(juncDest);
                    }
                }
            }

            // Set the destination depending on if the train is following the nat orientation of the track
            dest = nat ? getNode(dn.s.toIndex) : getNode(dn.s.fromIndex);

            double destCost = dest.s.length;
            double distanceThroughR = dn.minCost + destCost;
            if (distanceThroughR < dest.minCost) {
                nodeQueue.remove(dest);
                dest.minCost = distanceThroughR;
                dest.prev = dn;
                nodeQueue.add(dest);
            }
        }
    }

    /**
     * Finds the shortest path to the target
     *
     * @param target target node
     *
     * @return The list of nodes representing the shortest path
     * */
    private  List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<Node>();
        for (Node n = target; n != null; n = n.prev)
            path.add(n);
        Collections.reverse(path);
        return path;
    }
}
