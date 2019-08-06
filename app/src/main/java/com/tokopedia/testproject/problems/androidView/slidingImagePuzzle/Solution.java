package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static DownloadImageTask taskDownload;
    public interface onSuccessLoadBitmap{
        void onSliceSuccess(List<Bitmap> bitmapList, Bitmap bitmap);
        void onSliceFailed(String message);
    }
    public static void sliceTo4x4(Context context, onSuccessLoadBitmap onSuccessLoadBitmap, String imageUrl) {
        // TODO, download the image, crop, then sliced to 15 Bitmap (4x4 Bitmap). ignore the last Bitmap
        // below is stub, replace with your implementation!\
        taskDownload = new DownloadImageTask(onSuccessLoadBitmap);
        taskDownload.execute(imageUrl);
    }

    private static void sliceTo4x4(Bitmap bitmap, onSuccessLoadBitmap onSuccessLoadBitmap){
        if(bitmap != null){
            int SIDE_LENGTH = 400;
            if(bitmap.getWidth()== SIDE_LENGTH && bitmap.getHeight() == SIDE_LENGTH) onSuccessLoadBitmap.onSliceSuccess(generateBitmap(bitmap), bitmap);
            else if(bitmap.getWidth() > SIDE_LENGTH && bitmap.getHeight() > SIDE_LENGTH){
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, SIDE_LENGTH, SIDE_LENGTH, true);
                onSuccessLoadBitmap.onSliceSuccess(generateBitmap(resized),resized);
            } else onSuccessLoadBitmap.onSliceFailed("Your downloaded image is too small, please download with min. 400x400");
        } else onSuccessLoadBitmap.onSliceFailed("Failed download image");
    }

    private static ArrayList<Bitmap> generateBitmap(Bitmap bitmap){
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(Bitmap.createBitmap(bitmap, 0,0,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 100,0,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 200,0,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 300,0,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 0,100,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 100,100,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 200,100,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 300,100,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 0,200,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 100,200,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 200,200,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 300,200,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 0,300,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 100,300,100, 100));
        bitmapList.add(Bitmap.createBitmap(bitmap, 200,300,100, 100));
        return bitmapList;
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        onSuccessLoadBitmap onSuccessLoadBitmap;

        private DownloadImageTask(onSuccessLoadBitmap onSuccessLoadBitmap){
            this.onSuccessLoadBitmap = onSuccessLoadBitmap;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try {
                URL url = new URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            taskDownload = null;
            sliceTo4x4(result,onSuccessLoadBitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            taskDownload = null;
        }
    }
}
