package com.medeveloper.ayaz.mtraveller;

/**
 * Created by Ayaz on 12/29/2017.
 */

public class Registration_object {
    private String STATE;
    private int CITY;
    private String bus_no;
    private String bus_name;
    private int route_no;

    public int getRoute_no() {
        return route_no;
    }
    // private ArrayList<Station> route;

    public Registration_object(String STATE,int CITY,String bus_no, String bus_name,int route_no) {
        this.STATE = STATE;
        this.CITY = CITY;
        this.bus_no = bus_no;
        this.bus_name = bus_name;
     //   this.route = route;
    }

    public String getSTATE() {
        return STATE;
    }

    public int getCITY() {
        return CITY;
    }

    public String getBus_no() {
        return bus_no;
    }

    public String getBus_name() {
        return bus_name;
    }


}
