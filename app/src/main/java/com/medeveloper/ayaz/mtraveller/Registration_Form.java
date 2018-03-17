package com.medeveloper.ayaz.mtraveller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/*
*  Created by Ayaz Alam
*  on 14-03-2018
* */
public class Registration_Form extends AppCompatActivity {

    //Field for registration
    ArrayList<TextView> mList;
    //Edit Text for Bus Details
    EditText Bus_no, Bus_label,Route;
    Credentials form;
     // Auto Complete TextView for  State and City
    AutoCompleteTextView state_autocomplete,city_autocomplete;
    //Buttons
    Button Register;
    DatabaseReference mRef;
    //ArrayList of Station Objects (All route Stations)
    ArrayList<Station> mRoute;
    String ROUTE="";
    RelativeLayout this_layout;
    HashMap<String,String> StateCodes;
    String YourCity;
    String YourState;
    String YourRoute;
    //On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration__form);
        //Setting up Hashmap of state plus state code
        String[] stateCodes = getResources().getStringArray(R.array.india_state_codes);
        String[] states = getResources().getStringArray(R.array.india_states);
        StateCodes =new HashMap<>();
        for(int i = 0;i<states.length;i++)
            StateCodes.put(states[i],stateCodes[i]);

        //Settting up the Toolbar
        Toolbar myToolbar=findViewById(R.id.reg_toolbar);
        myToolbar.setTitle(getResources().getString(R.string.reg_form_activity));
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_directions_bus_white));
        setSupportActionBar(myToolbar);
        //Initialising objects
        mRef=FirebaseDatabase.getInstance().getReference();
        mRoute=new ArrayList<>();
        mList=new ArrayList<>();
       //Initialising the Edit Text
        this_layout=findViewById(R.id.reg_form_layout);
        Bus_no=findViewById(R.id.bus_number);
        Bus_label =findViewById(R.id.bus_label);
        state_autocomplete = findViewById(R.id.state);
        city_autocomplete  = findViewById(R.id.city);
        city_autocomplete.setEnabled(false);
        Route=findViewById(R.id.route_no);
        //Initialising autocomplete State and City
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.india_states));
        state_autocomplete.setAdapter(adapter);
        setUpAutoComplete();
        //Initialising Button and Adding Click Listener to Register
        Register=findViewById(R.id.register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prepareForm()) {
                        Register.setEnabled(false);
                    }
            }
        });


    }

    private void setUpAutoComplete() {
        state_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(StateCodes.containsKey(state_autocomplete.getText().toString()))
                {
                    city_autocomplete.setEnabled(true);
                    YourState = state_autocomplete.getText().toString();
                    String CityCode=StateCodes.get(YourState);
                    Toast.makeText(getApplicationContext(),CityCode,Toast.LENGTH_SHORT).show();
                    ArrayAdapter<String> city = new ArrayAdapter<>(Registration_Form.this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(getResId(CityCode,R.array.class)));
                    city_autocomplete.setAdapter(city);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                city_autocomplete.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    //Preparing the form to be uploaded on firebase database
    boolean routePresent = false;
    public boolean RoutePresent(final String route_no, DatabaseReference ds){
        ds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    Toast.makeText(getApplicationContext(),d.getKey(),Toast.LENGTH_SHORT).show();
                    if(route_no.equals(d.getKey().toString()))
                        routePresent = true;
                }
                else {
                    routePresent = false;
                    Toast.makeText(getApplicationContext(),"DataSnapshot Does not exist",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  routePresent;
    }

    void setUserData()
    {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRef.setValue(form);
    }

    boolean prepareForm() {
        String STATE = state_autocomplete.getText().toString();
        String CITY  = city_autocomplete.getText().toString();
        YourCity =CITY;
        //Creating a local variable to the Firebase Database where you wish Store the data
      DatabaseReference localRef= mRef.child("States").child(STATE).child(CITY).child("BUS REGISTRATIONS");
      final DatabaseReference routeRef = mRef.child("States").child(STATE).child(CITY).child("Routes");
      //Retrieving data from edit text
      String Bus_number=Bus_no.getText().toString();
      String Bus_Label=Bus_label.getText().toString();
      String route_no=Route.getText().toString();

      if( !check(Bus_number,Bus_Label,route_no,STATE,CITY))
         return false;
      YourRoute = route_no;
      //Creating a single object for registration form passing the data
      form=new Credentials(STATE,CITY,Bus_Label,Bus_number,route_no);

      //Pushing data to the database and adding on Success Listener

            localRef.child(Bus_number).setValue(form).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setUserData();
                    createCustomSnackbar("Successfully Created", getBaseContext(), "SUCCESS").show();
                    if(RoutePresent(ROUTE,routeRef)){

                        startActivity(new Intent(Registration_Form.this,Ticketing_window.class));
                        finish();
                        Register.setEnabled(false);
                    }else
                    {
                        Intent i =new Intent(getApplicationContext(),SetRoute.class);
                        i.putExtra("YourState",YourState);
                        i.putExtra("YourCity",YourCity);
                        i.putExtra("YourRoute",YourRoute);
                        startActivity(i);

                    }
                }
            });


        return false;
    }

    //For checking whether all fields are filled up correctly
    protected boolean check(String B,String B_L,String R,String state,String city) {

        boolean isOkay = true;
        if (B.equals("")) {
            Bus_no.setError("Please Enter the Bus Number");
            Bus_no.requestFocus();
            isOkay = false;
        } else if (B_L.equals("")) {
            Bus_label.setError("Please Enter the Bus Label");
            Bus_label.requestFocus();
            isOkay = false;
        } else if (R.equals("")) {
            Route.setError("Please Enter the Route Number");
            Route.requestFocus();
            isOkay = false;
        }
        else if (state.equals("")) {
            state_autocomplete.setError("Please Enter the Route Number");
            state_autocomplete.requestFocus();
            isOkay = false;
        }

        else if (city.equals("")) {
            city_autocomplete.setError("Please Enter the Bus Label");
            city_autocomplete.requestFocus();
            isOkay = false;
        }



return isOkay;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.setting:
                CreateDialogueForLogOut();
                return true;

            case R.id.set_route:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration_window_menu, menu);
        return true;
    }

    private void CreateDialogueForLogOut() {
        AlertDialog.Builder LogoutDialogue = new AlertDialog.Builder(this);
        LogoutDialogue.setTitle(getResources().getString(R.string.connfirm_logout));
        LogoutDialogue.setMessage(getResources().getString(R.string.logout_mesg));
        LogoutDialogue.setIcon(R.drawable.ic_error_outline_black_24dp);
        LogoutDialogue.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        LogoutDialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        LogoutDialogue.create();
        LogoutDialogue.show();



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
            textViewTop.setTextColor(getColor(R.color.error_red));
        }
        else
        {
            imageView.setImageDrawable(getDrawable(R.drawable.ic_info_outline_white_24dp));
            textViewTop.setTextColor(getColor(R.color.success_green));
        }

        textViewTop.setText(text);


// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        return snackbar;


    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


}
//End of class