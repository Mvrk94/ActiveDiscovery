package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.RatingBar;
import android.widget.RemoteViews;

import com.google.android.gms.*;

import com.facebook.FacebookSdk;
import com.google.android.gms.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.ReferenceQueue;

public class ReviewShareActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1313;
    ImageView imgtakenPhoto;
    TextView txtPoints;
    Button btnSelfie;
    Button fbButton;
    RatingBar rbNumStars;
    Button btnAccept;
    Bitmap thumbnail;
    int points =0;

    public Button btnGoogleShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_rewards_share);

        txtPoints = (TextView) txtPoints.findViewById(R.id.pointsTextView);

        btnSelfie = (Button) findViewById(R.id.btnSelfie);
        btnSelfie.setEnabled(false);
        btnSelfie.setOnClickListener(new btnSelfieClicker());

        rbNumStars = (RatingBar) findViewById(R.id.ratingBar);
        rbNumStars.setOnRatingBarChangeListener(new rbNumStarsClick());

        imgtakenPhoto = (ImageView) findViewById(R.id.imgSelfie);


        btnGoogleShare = (Button) findViewById(R.id.share_button);
        btnGoogleShare.setOnClickListener(new btnGoogleShareClick());
        btnGoogleShare.setEnabled(false);


    //    fbButton = (Button) findViewById(R.id.shareButton);

        btnAccept = (Button) findViewById(R.id.button);


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



        }

    }


    class btnSelfieClicker implements Button.OnClickListener{

        @Override
        public void onClick(View v) {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            btnGoogleShare.setEnabled(true);
          //  fbButton.setEnabled(false);

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
            createInstagramIntent(type);


        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void createInstagramIntent(String type){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType("image/*");

        // Create the URI from the media
        Uri imgUri = getImageUri(ReviewShareActivity.this,thumbnail);


        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, imgUri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
