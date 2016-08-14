package controllers;

import Util.CustomTracks;
import model.Section;
import view.Drawable.section_types.DrawableSection;

import java.util.*;

/**
 * Created by User on 26/07/2016.
 */
public class SectionGraph {

    // The sections in the railway
    private Section[] sections;

    // Each node represents a section
    private Set<Node> nodes;


    // Node to use for finding shortest path
    private class Node implements Comparable<Node>{
        Section s;
        Node prev;
        double minCost = Double.POSITIVE_INFINITY;

        public Node(Section s){
            this.s = s;
        }

        @Override
        public int compareTo(Node o) {return Double.compare(minCost, o.minCost);}
    }


    public SectionGraph(Section[] sections){
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

    public Set<Node> createNodes(Section[] sections){
        Set nodes = new HashSet<>();

        for(Section s : sections){
            nodes.add(new Node(s));
        }

        return nodes;
    }

    public List<Integer> getRoute(Section start, Section destination, boolean nat){
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
        getShortestPathTo(destNode).forEach(pn -> path.add(pn.s.getID()));

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
            if(dn.s.hasJunctionTrack()){ //TODO not sure if I need to know if inbound or outbound
                if(nat && dn.s.getJunction().inBound()){ // TODO test
                    juncDest = getNode(dn.s.getToIndex());

                    double destCost = juncDest.s.getLength();
                    double distanceThroughR = dn.minCost + destCost;
                    if (distanceThroughR < juncDest.minCost) {
                        nodeQueue.remove(juncDest);
                        juncDest.minCost = distanceThroughR;
                        juncDest.prev = dn;
                        nodeQueue.add(juncDest);
                    }
                }

                if(nat && !dn.s.getJunction().inBound()){
                    juncDest = getNode(dn.s.getJuncSectionIndex());

                    double destCost = juncDest.s.getLength();
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
//            dest = nat ? getNode(dn.s.getToIndex()) : getNode(dn.s.getFromIndex());
            dest = nat ? getNode(dn.s.getToIndexNat()) : getNode(dn.s.getFromIndexNat());

            double destCost = dest.s.getLength();
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

    public static void main(String[] args){
        CustomTracks ct = new CustomTracks("FULL");
        DrawableSection[] drawSections = ct.getSections();

        Section[] sections = new Section[drawSections.length];
        for(int i = 0; i < drawSections.length; i++){
            sections[i] = drawSections[i].getSection();
        }
        SectionGraph sg = new SectionGraph(sections);

        System.out.println(sg.getRoute(sections[0], sections[9],true));
    }
}
