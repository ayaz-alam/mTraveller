package com.medeveloper.ayaz.mtraveller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SetRoute extends AppCompatActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE =111;
    private List<Station> mRoute;
    private List<TextView> mList;
    private int id=0;
    private LinearLayout ll;
    String YourCity;
    String YourState;
    String YourRoute;
    private RelativeLayout this_layout;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_route);
        Bundle bundle =getIntent().getExtras();
        this_layout=findViewById(R.id.this_layout);
        YourState=bundle.get("YourState").toString();
        YourCity=bundle.get("YourCity").toString();
        YourRoute=bundle.get("YourRoute").toString();
        mList = new ArrayList<>();
        mRoute = new ArrayList<>();
        ll = findViewById(R.id.set_route_layout);
        Button Submit = findViewById(R.id.submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadToFirebase();
            }
        });
        Button AddStationButton = findViewById(R.id.add_station);
        AddStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Came hrer",Toast.LENGTH_SHORT).show();
                Add_Station();
            }
        });

    }

    private void UploadToFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("States").child(YourState).child(YourCity).child("Routes").child(YourRoute);
        ref.setValue(mRoute).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                createCustomSnackbar("Successful",getApplicationContext(),"SUCCESS");
                Intent i = new Intent(getBaseContext(),Ticketing_window.class);
                i.putExtra("YourState",YourState);
                i.putExtra("YourCity",YourCity);
                i.putExtra("YourRoute",YourRoute);
                startActivity(i);
            }
        });


    }
    private boolean Check()
    {
        if(mRoute.size()<3)
        {
            createCustomSnackbar("Please enter a valid route details",getApplicationContext(),"ERROR");
            return false;
        }
        else return  true;
    }

    //Custom Snackbar
    Snackbar createCustomSnackbar(String text, Context context, String code) {// Create the Snackbar
        Snackbar snackbar = Snackbar.make(this_layout, "", Snackbar.LENGTH_LONG);
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


    //This is to Create fragment that pop ups AutoComplete Place Picker and which returns Selected place in OnActivityResult()
    private void setStation() {
        //Google Place Autocomplete Intent Builder to fetch a location name lat and long
        try {
            Intent intent=new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    //To be triggered when user wish to Add Route station
    private void Add_Station() {
        //Initialising the layout where addition of view would take place
        //Creating a textView
        TextView Ed=new TextView(this);
        //Creating Layout Parameter for Text View and Adding it
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Ed.setLayoutParams(p);
        Ed.setTextSize(24);
        //Ed.setBackground(getDrawable(R.drawable.layout_with_border));
        Ed.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Ed.setPadding(15,5,5,5);
        if(id==0)
            Ed.setText("Enter Source Station");
        else
        //if first Element then it is source
        Ed.setText("Enter next Station");

        //Filling up other Attributes for textView
        Ed.setId(id);
        mList.add(Ed);
        Ed.setMaxLines(1);
        //Adding it to layout
        ll.addView(Ed);
        //Poping PICK_UP_PLACE FRAGMENT
        setStation();
    }

    //For Retrieving the Place searched by the user in th PLACE_AUTOCOMPLETE Dialog
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);//Place selected by the user
                mRoute.add(new Station(place.getName().toString(),""+place.getLatLng().latitude,""+place.getLatLng().longitude));
                mList.get(id).setText(place.getName().toString());
                id++;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                ll.removeView(mList.get(id));
                mList.remove(id);
            }
        }

    }

}
