package com.medeveloper.ayaz.mtraveller;

import java.util.Date;

/**
 * Created by Ayaz on 12/27/2017.
 */

public class Ticket {
    private String bus;
    private Station source,dest;
    private Date time;
    private String route_No;

    public String getRoute_No() {
        return route_No;
    }

    public Ticket(String bus, Station source, Station dest, Date time, String route_no)
    {
        this.bus =bus;
        this.source =source;
        this.dest=dest;
        this.time=time;
        this.route_No=route_no;
    }

    public String getBus() {
        return bus;
    }

    public Station getSource() {
        return source;
    }

    public Station getDest() {
        return dest;
    }

    public Date getTime() {
        return time;
    }
}
