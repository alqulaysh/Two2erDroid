package com.se491.app.two2er.Fragments.Bookings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eoliv on 3/7/2017.
 */

public class BookingObject {
    String id;
    String status;
    String MeetingDate;
    String CreationDate;
    String tutorUserId;
    String studentUserId;

    public BookingObject(){        this.status = "Test Status";
    }

    public BookingObject(JSONObject user) throws JSONException {
        this.id = user.getString("_id");
        this.status = user.getString("status");
        this.MeetingDate = user.getString("scheduledmeetingdate");
        this.CreationDate = user.getString("bookingcreationdate");
        this.tutorUserId = user.getString("student_user_id");
        this.studentUserId = user.getString("student_user_id");
    }
}
