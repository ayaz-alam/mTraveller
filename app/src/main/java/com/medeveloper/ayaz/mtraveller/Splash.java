package com.medeveloper.ayaz.mtraveller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//First Activity to checking login & Location Service Permission status and Splash Screen setup
public class Splash extends AppCompatActivity {
    FirebaseAuth mAuth;
    RelativeLayout this_layout;
    //Request Code for Location
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth =FirebaseAuth.getInstance();
        this_layout = findViewById(R.id.splash_activity);
        if(getLocationServices())
        authenticate();

    }

    private void authenticate() {
      //  Toast.makeText(this,"Came here in Authentication",Toast.LENGTH_SHORT).show();
     //Initialising the Auth Ref
        if (mAuth.getCurrentUser()== null)//if not already logged in
        {
            startActivity(new Intent(Splash.this,LoginActivity.class));
            finish();
        }
        else
            {
           startActivity(new Intent(Splash.this, Ticketing_window.class));
           finish();
            }
    }

    private boolean getLocationServices() {
         //Checking if the location permissions are provided to the app or not
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If not then creating a pop to allow the permission
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return false;
        }
        else return true;
    }
    //Reads the result returned by pop up
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)//If Location Services has been granted
                {
                    authenticate();
                }
                else
                {
                createCustomSnackbar("Please Grant Access to location services",getApplicationContext(),"ERROR").setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLocationServices();
                    }
                }).show();
                }
                return;
            }
        }
    }

    Snackbar createCustomSnackbar(String text, Context context, String code) {// Create the Snackbar
        Snackbar snackbar = Snackbar.make(this_layout, "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
// Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

// Inflate our custom view
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View snackView = mInflater.inflate(R.layout.my_snackbar, null);
// Configure the view

        ImageView imageView = (ImageView) snackView.findViewById(R.id.snackbar_image);
        TextView textViewTop = (TextView) snackView.findViewById(R.id.text_snackbar);
        if(code.equals("ERROR"))//if error
        {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_error_outline_white_24dp));
            textViewTop.setTextColor(getResources().getColor(R.color.error_red));
        }
        else
        {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_info_outline_white_24dp));
            textViewTop.setTextColor(getResources().getColor(R.color.success_green));
        }

        textViewTop.setText(text);


// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        return snackbar;


    }
//End of Class
}