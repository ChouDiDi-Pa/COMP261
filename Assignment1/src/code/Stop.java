package code;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Stop {

    private String id, name;
    private double lat, lon;
    private Location location;

    public Stop(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.location = Location.newFromLatLon(lat, lon);
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public double getLat() {
        return lat;
    }


    public double getLon() {
        return lon;
    }

    public Location getLocation(){
        return location;
    }


    public void drawStop(Graphics g, Location origin, double scale){
        //System.out.println(location);
        Point p = location.asPoint(origin, scale);
        g.fillRect(p.x-5, p.y-5, 6, 6);
        //System.out.println(p);
    }


    public boolean isSelected(int x, int y, Location origin, double scale){
        Point p = this.location.asPoint(origin, scale);
        return p.getX() >= x - 4 && p.getX() <= x + 4 && p.getY() >= y - 4 && p.getY() <= y + 4;
    }


    public void getDetails(List<Trip> trips, JTextArea output){
        String details = this.toString() + "\n";
        for(int i = 0; i < trips.size(); i++){
            String id = "id: " + trips.get(i).getId() + " ";
            details += id;
        }
        details += "\n";
        output.append(details);
    }

    @Override
    public String toString() {
        return "Stop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", location=" + location +
                '}';
    }
}
