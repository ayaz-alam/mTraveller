package com.medeveloper.ayaz.mtraveller;

/**
 * Created by Ayaz on 1/16/2018.
 */

@SuppressWarnings("serial")
public class Credentials {
    protected String state;
    protected String city;
    protected String bus_name;
    protected String bus_number;

    public Credentials() {
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBus_name(String bus_name) {
        this.bus_name = bus_name;
    }

    public void setBus_number(String bus_number) {
        this.bus_number = bus_number;
    }

    public void setRoute_number(String route_number) {
        this.route_number = route_number;
    }

    public Credentials(String state, String city, String bus_name, String bus_number, String route_number) {
        this.state = state;
        this.city = city;
        this.bus_name = bus_name;
        this.bus_number = bus_number;
        this.route_number = route_number;
    }

    protected String route_number;

    protected String getBus_number() {
        return bus_number;
    }





    protected String getState() {
        return state;
    }

    protected String getCity() {
        return city;
    }

    protected String getBus_name() {
        return bus_name;
    }

    protected String getRoute_number() {
        return route_number;
    }







}
