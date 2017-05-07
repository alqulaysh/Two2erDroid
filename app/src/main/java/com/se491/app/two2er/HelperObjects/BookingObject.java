package com.se491.app.two2er.HelperObjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eoliv on 3/7/2017.
 */

public class BookingObject {
    public String bookingId;
    public String status;
    public String MeetingDate;
    public String CreationDate;
    public String tutorUserId;
    public String tutor_name;
    public String student_name;
    public String studentUserId;
    public String timekit_booking_id;

    public BookingObject(){        this.status = "Test Status";
    }

    public BookingObject(JSONObject user) throws JSONException {
        this.bookingId = user.getString("_id");
        this.status = user.getString("status");
        this.MeetingDate = user.getString("scheduledmeetingdate");
        this.CreationDate = user.getString("bookingcreationdate");
        this.tutorUserId = user.getString("student_user_id");
        this.tutor_name = user.getString("tutor_name");
        this.student_name = user.getString("student_name");
        this.studentUserId = user.getString("student_user_id");
        this.timekit_booking_id = user.getString("timekit_booking_id");
    }
}
