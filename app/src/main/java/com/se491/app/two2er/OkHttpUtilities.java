package com.se491.app.two2er;

import java.io.IOException;

import okhttp3.Request;
import okio.Buffer;

/**
 * Created by pazra on 4/23/2017.
 */

public class OkHttpUtilities {
    public static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
