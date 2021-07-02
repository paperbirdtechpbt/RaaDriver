/*
 * Copyright (C) 2014 Balázs Varga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pbt.raadrivers.Activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.MyApplication;
import com.warnyul.android.widget.FastVideoView;

import java.util.HashMap;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

/**
 * Sample Activity for VideoView class simple usage.
 */
public class TrainingVideoActivity extends AppCompatActivity {

    ImageView imageView;
    String path = "http://maharishi.paperbirdtech.com/video_attendance/1556343554_1.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_video);

        MyApplication.setCurrentActivity(this);

        progstart(TrainingVideoActivity.this, "Loading...", "Loading...");
        path = getIntent().getStringExtra("path");
        final FastVideoView videoView = (FastVideoView) findViewById(R.id.video);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath(path);
        videoView.start();
        progstop();
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        FastVideoView bmImage;

        public DownloadImage(FastVideoView bmImage) {
            this.bmImage = (FastVideoView) bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap myBitmap = null;
            MediaMetadataRetriever mMRetriever = null;
            try {
                mMRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mMRetriever.setDataSource(urls[0], new HashMap<String, String>());
                else
                    mMRetriever.setDataSource(urls[0]);
                myBitmap = mMRetriever.getFrameAtTime(-1, MediaMetadataRetriever.OPTION_CLOSEST);
            } catch (Exception e) {
                e.printStackTrace();


            } finally {
                if (mMRetriever != null) {
                    mMRetriever.release();
                }
            }
            return myBitmap;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap result) {
            BitmapDrawable bitmapD = new BitmapDrawable(result);
            imageView.setBackground(bitmapD);
        }
    }

}
