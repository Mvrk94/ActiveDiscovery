package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.RatingBar;
import android.widget.TextView;

//import com.facebook.FacebookSdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReviewShareActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1313;
    ImageView imgtakenPhoto;
    TextView txtPoints;
    TextView txtsharePoints1;
   // TextView txtsharePoints2;
    Button btnSelfie;
    Button fbButton;
    RatingBar rbNumStars;
    Button btnAccept;
    Bitmap thumbnail;
    EditText edtComment;
    String path;
    Uri tempUri;
    File finalFile;
    int Count=0;
    int points =0;
    int sharePoints =0;
    File dir;
    String possiblePath;


    public Button btnGoogleShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_rewards_share);



        points = WorkoutActivity.discoveryPoints;
      //  points =5;

        sharePoints = WorkoutActivity.sharePoints;

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        txtPoints = (TextView) findViewById(R.id.pointsTextView);
        txtPoints.setText(String.valueOf(points));


        txtsharePoints1 = (TextView) findViewById(R.id.sharePoints1);
     //   txtsharePoints2 = (TextView) findViewById(R.id.sharePoints2);

        txtsharePoints1.setText("(+"+sharePoints+" extra points)");
     //   txtsharePoints2.setText("(+"+sharePoints+" extra points)");

        btnSelfie = (Button) findViewById(R.id.btnSelfie);
        btnSelfie.setEnabled(false);
        btnSelfie.setOnClickListener(new btnSelfieClicker());

        rbNumStars = (RatingBar) findViewById(R.id.ratingBar);
        rbNumStars.setOnRatingBarChangeListener(new rbNumStarsClick());

        imgtakenPhoto = (ImageView) findViewById(R.id.imgSelfie);


        btnGoogleShare = (Button) findViewById(R.id.share_button);
        btnGoogleShare.setOnClickListener(new btnGoogleShareClick());
        btnGoogleShare.setEnabled(false);
     //   saveImage();

    //    fbButton = (Button) findViewById(R.id.shareButton);

        btnAccept = (Button) findViewById(R.id.btnAccept);


//      //  btnGoogleShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LaunchGooglePlus();
//            }
//        });
//    }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_IMAGE_CAPTURE){
            thumbnail = (Bitmap) data.getExtras().get("data");
            imgtakenPhoto.setImageBitmap(thumbnail);


          //  tempUri = getImageUri(getApplicationContext(), thumbnail);

            // CALL THIS METHOD TO GET THE ACTUAL PATH

        //   finalFile = new File(getRealPathFromURI(tempUri));
            //finalFile = new File(dir,"selfie.jpeg");


        }

    }

    @Override
    protected void onResume(){
        super.onResume();
     //   Toast.makeText(getApplicationContext(),path,Toast.LENGTH_SHORT).show();
        if(Count==2) {
            txtsharePoints1.setTextColor(getResources().getColor(R.color.dark_green));
            txtsharePoints1.setText(sharePoints + " points added.");
            int updatedpoints = points + sharePoints;
            txtPoints.setText(String.valueOf(updatedpoints));
        }
        if(Count==1){
          //  Toast.makeText(getApplicationContext(),getApplicationInfo().dataDir,Toast.LENGTH_LONG).show();
       //     saveFile(getApplicationContext(), thumbnail, "Image");
          //  Toast.makeText(getApplicationContext(),possiblePath,Toast.LENGTH_SHORT).show();

          //  String path = saveToInternalStorage(thumbnail);
           //   String path1 = obtainPath(getApplicationContext(),"image");
           // finalFile = new File(path,"profile.jpg");
         //   finalFile = new File(path,"Image.png");
        }


    }

    class btnSelfieClicker implements Button.OnClickListener{

        @Override
        public void onClick(View v) {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            btnGoogleShare.setEnabled(true);
            Count+=1;

          //  path = MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail, "Image", null);


        }
    }

    class rbNumStarsClick implements RatingBar.OnRatingBarChangeListener{


        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            btnSelfie.setEnabled(true);
         //   btnSelfie.postInvalidate();
            btnAccept.setEnabled(true);

            btnGoogleShare.setEnabled(true);

         //   fbButton.setEnabled(false);
        }
    }

    class btnGoogleShareClick implements Button.OnClickListener {

        @Override
        public void onClick(View v) {

            String type = "image/*";
            createShareIntent(type);

        }
    }


    private void createShareIntent(String type){
    //  Toast.makeText(getApplicationContext(),finalFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();


        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.setAction(Intent.ACTION_SEND);

        // Create the new Intent using the 'Send' action.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail,"Title", null);
        Uri imageUri = Uri.parse(path);
        i.putExtra(Intent.EXTRA_STREAM, imageUri);

        i.putExtra(Intent.EXTRA_TEXT, "#WorkoutWithVitality");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(i,"ShareImage"));
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("*/*");
//        i.setAction(Intent.ACTION_SEND);
//      //  i.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(finalFile));
//       // i.putExtra(Intent.EXTRA_STREAM, Uri.parse(finalFile.getAbsolutePath()));
//        i.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(finalFile));
//        i.putExtra(Intent.EXTRA_TEXT, "#WorkoutWithVitality");
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



      //  startActivity(Intent.createChooser(i, "Share Image"));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

       path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
          //  Toast.makeText(getApplicationContext(),path,Toast.LENGTH_SHORT).show();
        return Uri.parse(path);
    }
//
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir


        File directory = cw.getDir("imageDir", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        // Create imageDir
        File mypath = new File(directory,"profile.png");




        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
        return directory.getAbsolutePath();
    }


    public void saveFile(Context context, Bitmap b, String picName){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public  String obtainPath(Context context,String picName)  {
        String path = null;
        FileInputStream fis;

        try {
            fis = context.openFileInput(picName);
             path = context.getPackageResourcePath();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;

    }
}
