package com.manda.pertemuan13;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private static final String IMAGE_URL = "https://stis.ac.id/media/source/up.png";

    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        button = findViewById(R.id.asyncTask);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
    }

    private void loadImage() {
    
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Bitmap> future = executor.submit(new ImageDownloaderTask(IMAGE_URL));

        try {
            Bitmap bmImg = future.get(); 
            if (imageView != null) {
                imageView.setImageBitmap(bmImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); 
        }
    }

    private class ImageDownloaderTask implements Callable<Bitmap> {
        private final String imageUrl;

        public ImageDownloaderTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public Bitmap call() throws Exception {
            InputStream is = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                is = connection.getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }
    }
}
