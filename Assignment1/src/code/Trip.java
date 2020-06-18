package code;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trip {

    private String id;
    private Set<Connection> connections;
    //List<Stop> stopSequence;

    public Trip(String id) {
        this.id = id;
        //this.stopSequence = stopSequence;
        this.connections = new HashSet<>();

    }


    public String getId() {
        return id;
    }


    public void addConnection(Connection connection){
        connections.add(connection);
    }

    public Set<Connection> getConnections() {
        return connections;
    }

    public void drawTrip(Graphics g, Location origin, double scale){
        for(Connection con: connections){
            con.drawConnection(g, origin, scale);
        }
        //g.drawLine(100,100,100,100);
        //g.drawRect(origin.x,origin.y,200,200);

    }

    public boolean hasCon(Stop stop){
        for(Connection con: connections){
            if(con.hasStop(stop)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                '}';
    }
}
