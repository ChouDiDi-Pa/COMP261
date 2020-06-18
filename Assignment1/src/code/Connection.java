package code;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Connection {

    private String id;
    private Stop startStop, endStop;
    private List<Location> locations;

    public Connection(String id, Stop startStop, Stop endStop) {
        this.id = id;
        this.startStop = startStop;
        this.endStop = endStop;
        this.locations = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Stop getStartStop() {
        return startStop;
    }

    public Stop getEndStop() {
        return endStop;
    }


    public void addLocation(Location start, Location end){
        locations.add(start);
        locations.add(end);
    }


    public boolean hasStop(Stop stop){
        return stop.equals(startStop) || stop.equals(endStop);
    }


    public void drawConnection(Graphics g, Location origin, double scale){
        Point start = locations.get(0).asPoint(origin, scale);
        for(Location loc: locations){
            Point end = loc.asPoint(origin, scale);
            if(!end.equals(start)){
                g.drawLine(start.x, start.y, end.x, end.y);
                start = end;
            }
        }

    }


    @Override
    public String toString() {
        return "Connection{" +
                "id='" + id + '\'' +
                ", startStop=" + startStop +
                ", endStop=" + endStop +
                '}';
    }
}
