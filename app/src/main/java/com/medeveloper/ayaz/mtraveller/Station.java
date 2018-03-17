package com.medeveloper.ayaz.mtraveller;

/**
 * Created by Ayaz on 12/28/2017.
 */

public class Station {
    public String label, lat,lng;
    public Station(String label,String lat ,String lng)
    {
        this.label=label;
        this.lat =lat;
        this.lng=lng;
    }
    Station()
    {
    }

    public String getLabel() {
        return label;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
