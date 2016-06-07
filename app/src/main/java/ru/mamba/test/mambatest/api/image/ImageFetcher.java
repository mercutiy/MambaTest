package ru.mamba.test.mambatest.api.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageFetcher {

    public Bitmap fetchImage(String specUrl) throws IOException {
        URL url = new URL(specUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while ((byteRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.close();

            byte[] bytes = outputStream.toByteArray();

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } finally {
            connection.disconnect();
        }
    }

}