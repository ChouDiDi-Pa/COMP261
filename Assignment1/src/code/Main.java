package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main extends GUI {

    private Location origin;
    private double scale;
    private boolean switchSearch = false;
    private boolean open = false;
    private boolean hasTrips = false;

    private TrieNode trieNode;

    private Map<String, Trip> tripMap;
    private Map<String, Stop> stopMap;
    private Map<String, Connection> connectionMap;
    private ArrayList<Trip> selectedTrips;
    private List<Stop> selectedStops;

    private Stop selectedStop = null;


    public Main(){
        this.origin = Location.newFromLatLon(-12.499435, 130.94329);
        origin = origin.moveBy(-15, 20);
        this.scale = 50;

        this.trieNode = new TrieNode();

        this.tripMap = new HashMap<>();
        this.stopMap = new HashMap<>();
    }

    @Override
    protected void onWheel(MouseWheelEvent e) {
        if(e.getWheelRotation() > 0)
            onMove(Move.ZOOM_IN);
        else if(e.getWheelRotation() < 0)
            onMove(Move.ZOOM_OUT);
        else
            redraw();
    }


    @Override
    protected void onDrag(double x, double y) {
        origin = new Location(origin.x + x/scale, origin.y);
        origin = new Location(origin.x,origin.y - y/scale);
        redraw();
    }

    @Override
    protected void redraw(Graphics g) {
        //draw trips
        g.setColor(Color.GRAY);
        for(String id: tripMap.keySet()){
            tripMap.get(id).drawTrip(g, origin, scale);
        }

        //draw stops
        g.setColor(Color.CYAN);
        for(Stop stop: stopMap.values()) {
            stop.drawStop(g, origin, scale);
            //System.out.println(stop);
        }

        //draw selected stop
        g.setColor(Color.RED);
        if(selectedStop != null){
            selectedStop.drawStop(g, origin, scale);
        }


        if(switchSearch){
            if(open){
                if(hasTrips){
                    //draw selected trips
                    g.setColor(Color.GREEN);
                    for(Trip trip: selectedTrips){
                        trip.drawTrip(g, origin, scale);
                    }
                }

                hasTrips = false;
            }
        }
        else{
            if(open){
                if(hasTrips){
                    g.setColor(Color.RED);
                    for(Stop stop: selectedStops){
                        stop.drawStop(g,origin,scale);
                    }
                    g.setColor(Color.GREEN);
                    for(Trip trip: selectedTrips){
                        trip.drawTrip(g, origin, scale);
                    }
                }
            }

        }
    }


    @Override
    protected void onClick(MouseEvent e) {
        open = false;
        int clickX = e.getX();
        int clickY = e.getY();
        for(Stop stop: stopMap.values()){
            if(stop.isSelected(clickX, clickY, origin, scale)){
                selectedStop = stop;
            }
        }
        findTrip();

    }



    @Override
    protected void onSearch() {
        String text = getSearchBox().getText();

        System.out.println(text);
        // return true use linear search
        if(switchSearch)linearSearch(text);
        else tireSearch(text);

    }


    private void linearSearch(String text){
        for(String stopID: stopMap.keySet()){
            if(isMatch(text, stopMap.get(stopID).getName())){
                selectedStop = stopMap.get(stopID);
            }
            else{
                System.out.println("Wrong name.");
            }
        }
        System.out.println(selectedStop);
        findTrip();
        open = true;
        hasTrips = true;
        System.out.println(selectedTrips);
    }


    private void tireSearch(String text){
        selectedStops = new ArrayList<Stop>();
        List<String> results = new ArrayList<String>();

        getTextOutputArea().setText("Suggest Stop: ");

        results = this.trieNode.getAll(text);
        if(results != null){
            for(String name: results){
                for(String id: stopMap.keySet()){
                    if(isMatch(name, stopMap.get(id).getName())){
                        getTextOutputArea().append("\n" + name);
                        selectedStops.add(stopMap.get(id));

                    }
                }
            }
            findTrips();
            open = true;
            hasTrips = true;

            System.out.println(results);
            System.out.println(selectedStops);
        }
    }



    @Override
    protected void onMove(Move m) {
        hasTrips = true;

        Dimension pane = getDrawingAreaDimension();
        double factor = 2;

        switch(m){
            case EAST:
                origin = origin.moveBy(100/scale, 0);
                break;
            case WEST:
                origin = origin.moveBy(-100/scale, 0);
                break;
            case NORTH:
                origin = origin.moveBy(0, 100/scale);
                break;
            case SOUTH:
                origin = origin.moveBy(0, -100/scale);
                break;
            case ZOOM_IN:
                scale *= factor;
                origin = origin.moveBy(pane.getWidth()/(scale*factor), -pane.getHeight()/(scale*factor));
                break;
            case ZOOM_OUT:
                origin = origin.moveBy(-pane.getWidth()/(scale*factor), pane.getHeight()/(scale*factor));
                scale /= factor;
                break;
        }
    }


    @Override
    protected void onLoad(File stopFile, File tripFile) {

        loadStops(stopFile);
        loadTrips(tripFile);

    }

    /**
    loads the stops.txt
     */
    private void loadStops(File stops){
        System.out.println("start reading stop");
        try {

            BufferedReader br = new BufferedReader(new FileReader(stops));

            //skip first line
            br.readLine();

            String line = br.readLine();
            while(line != null){
                String[] thisStop = new String[4];
                thisStop = line.split("\t");
                String id = String.valueOf(thisStop[0]);
                String name = String.valueOf(thisStop[1]);
                Double lat = Double.parseDouble(thisStop[2]);
                Double lon = Double.parseDouble(thisStop[3]);

                Stop stop = new Stop(id, name, lat, lon);
                stopMap.put(id, stop);

                trieNode.add(name);

                line = br.readLine();

            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error loading stops.txt" + e);
        }
        System.out.println("end reading");
        /**
        Set<String> set = stopMap.keySet();
        int count = 1;
        for(String id: set){
            System.out.println(count + id + "" + stopMap.get(id));
            count++;
        }*/

    }

    /**
    loads the trips.txt
     */
    private void loadTrips(File trips){
        System.out.println("start reading trips");
        try {

            BufferedReader br = new BufferedReader(new FileReader(trips));
            //skip the first line
            br.readLine();

            String line  = br.readLine();
            while(line != null){
                Queue<String> thisTrip = new ArrayDeque<String>(Arrays.asList(line.split("\t")));
                String tripID = String.valueOf(thisTrip.poll());
                Trip trip = new Trip(tripID);
                tripMap.put(tripID, trip);

                while(!thisTrip.isEmpty()){
                    String stopID1 = String.valueOf(thisTrip.poll());
                    //if is the last one
                    if(thisTrip.isEmpty()){
                        break;
                    }
                    String stopID2 = String.valueOf(thisTrip.peek());

                    Stop start = stopMap.get(stopID1);
                    Stop end = stopMap.get(stopID2);
                    Location startLoc = start.getLocation();
                    Location endLoc = end.getLocation();
                    Connection con = new Connection(tripID, start, end);
                    con.addLocation(startLoc, endLoc);

                    if(tripMap.containsKey(tripID)){
                        tripMap.get(tripID).addConnection(con);
                    }

                }

                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error loading trips.txt" + e);
        }
        System.out.println("end reading");
        /**
        Set<String> set = tripMap.keySet();
        for(String id: set) {
            System.out.println(id + "" + tripMap.get(id));
        }*/
    }


    /**
     * To check if String s1 equals String s2
     * @param text
     * @param name
     * @return
     */
    private boolean isMatch(String text, String name){
        String[] s1 = text.split("");
        String[] s2 = name.split("");
        if(s1.length != s2.length) return false;
        for (int i = 0; i < s1.length; i++) {
            if(!s1[i].equals(s2[i])) return false;
        }
        return true;
    }



    /**
     * to find all the trips through the highlighted stops and print the details
     */
    private void findTrip(){

        getTextOutputArea().setText("result:\n");


        selectedTrips = new ArrayList<>();
        for(String id: tripMap.keySet()){
            if(tripMap.get(id).hasCon(selectedStop)){
                selectedTrips.add(tripMap.get(id));
            }
        }

        selectedStop.getDetails(selectedTrips, getTextOutputArea());
    }



    /**
     * Trie Search used only
     * To get all the trips through selected stops
     */
    private void findTrips(){
        //getTextOutputArea().setText("result:\n");

        selectedTrips = new ArrayList<>();
        for(Stop stop: selectedStops){
            for(String id: tripMap.keySet()){
                if(tripMap.get(id).hasCon(stop)){
                    selectedTrips.add(tripMap.get(id));
                }
            }
            //stop.getDetails(selectedTrips, getTextOutputArea());
        }
    }


    public static void main(String[] args) {

        new Main();
    }
}
