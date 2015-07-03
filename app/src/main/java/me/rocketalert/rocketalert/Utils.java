package me.rocketalert.rocketalert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class Utils {
    public static Bitmap downloadBitmap(String url) throws IOException {
        byte[] data = downloadData(url);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static byte[] downloadData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }
}
