package uk.ac.st_andrews.cs.rbwilliams.fingerpaint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import java.net.URI;

/**
 * Created by rbwilliams on 05/11/2015.
 */
public class PreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //remove title fram
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String imgURI = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Uri uri = Uri.parse(imgURI);


        ImageView image = new ImageView(this);
        image.setImageURI(uri);
        setContentView(image);
    }




}
