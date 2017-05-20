package com.se491.app.two2er.HelperObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by ajscilingo on 5/17/17.
 *
 * Used to Store TimeBlocks as returned by getTutorsSchedule endpoint
 *
 */

public class TimeBlockObject implements Comparator<TimeBlockObject> {
    private Date _start;
    private Date _end;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public TimeBlockObject(JSONObject timeBlock) throws JSONException, ParseException {

        this._start = dateFormat.parse(timeBlock.getString("start"));
        this._end = dateFormat.parse(timeBlock.getString("end"));
    }

    @Override
    public int compare(TimeBlockObject o1, TimeBlockObject o2) {
        // check starting times first
        if (o1._start.compareTo(o2._start) < 0)
            return -1;
        if (o1._start.compareTo(o2._start) > 0)
            return 1;
        // if the starting times are the same then check the ending times
        if(o1._start.compareTo(o2._start) == 0){

            if(o1._end.compareTo(o2._end) < 0)
                return -1;
            if(o1._end.compareTo(o2._end) > 0)
                return 1;
        }
        // else both the start and end times for the TimeBlockObjects are the same
        return 0;
    }

    public Date getStart(){
        return this._start;
    }

    public Date getEnd(){
        return this._end;
    }

    @Override
    public String toString() {
        return "TimeBlockObject{" +
                "start : " + _start +
                ",end : " + _end +
                '}';
    }
}
