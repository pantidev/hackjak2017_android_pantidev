package hackjak.pantidev.tako;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bsupriadi on 8/28/2017.
 */

public class CustomCamera extends AppCompatActivity {

        final String TAG = this.getClass().getSimpleName();
        private String photoPath;
        private Context context;

        public String getPhotoPath() {
            return this.photoPath;
        }

        public CustomCamera(Context context) {
            this.context = context;
        }

        public Intent takePhotoIntent() throws IOException {
            Intent in = new Intent("android.media.action.IMAGE_CAPTURE");
            if(in.resolveActivity(this.context.getPackageManager()) != null) {
                File photoFile = this.createImageFile();
                if(photoFile != null) {
                    in.putExtra("output",
                            FileProvider.getUriForFile(CustomCamera.this, BuildConfig.APPLICATION_ID +".provider", photoFile));
                }
            }

            return in;
        }

        private File createImageFile() throws IOException {
            String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            this.photoPath = image.getAbsolutePath();
            return image;
        }

        public void addToGallery() {
            //Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");

            File f = new File(this.photoPath);
            //Uri contentUri = Uri.fromFile(f); api 23--
            Uri contentUri = FileProvider.getUriForFile(CustomCamera.this,BuildConfig.APPLICATION_ID +".provider", f);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(contentUri);
            this.context.sendBroadcast(mediaScanIntent);
        }

}
