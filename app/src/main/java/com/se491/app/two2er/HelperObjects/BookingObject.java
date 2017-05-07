package com.se491.app.two2er.HelperObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by eoliv on 3/7/2017.
 */

public class BookingObject implements Comparator<BookingObject>{
    public String bookingId;
    public String status;
    public String MeetingDate;
    public Date CreationDate;
    public String tutorUserId;
    public String tutor_name;
    public String student_name;
    public String studentUserId;
    public String timekit_booking_id;

    public BookingObject(JSONObject user) throws JSONException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.zzz'z'");

        this.bookingId = user.getString("_id");
        this.status = user.getString("status");
        this.MeetingDate = user.getString("scheduledmeetingdate");
        try {
            this.CreationDate = dateFormat.parse(user.getString("bookingcreationdate"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.tutorUserId = user.getString("student_user_id");
        this.tutor_name = user.getString("tutor_name");
        this.student_name = user.getString("student_name");
        this.studentUserId = user.getString("student_user_id");
        this.timekit_booking_id = user.getString("timekit_booking_id");
    }

    @Override
    public int compare(BookingObject o1, BookingObject o2) {
        if(o1.CreationDate.compareTo(o2.CreationDate) < 0){
            return -1;
        }
        if(o1.CreationDate.compareTo(o2.CreationDate) > 0){
            return 1;
        }
        return 0;
    }
}
