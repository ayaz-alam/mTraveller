package com.medeveloper.ayaz.mtraveller;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Ticketing_window extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 123;
    ListView mListView;
    private ArrayList<Station> stationlist=new ArrayList<>();
    TicketWindowAdapter adapter;
    //These things would be taken from profile of the bus
    Station Src;
    Station Dest;
    private DatabaseReference mRef;
    ProgressBar mBar;
    Button Book;
    TextView Bus_name,Src_name,Dest_name;
    RelativeLayout this_layout;
    Credentials credentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketing_window);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setting up the toolbar
        Toolbar myToolbar = findViewById(R.id.ticket_window_toolbar);
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_directions_bus_white));
        setSupportActionBar(myToolbar);

            //Showing Progress Bar
            mBar=findViewById(R.id.progress_bar);
            mBar.setVisibility(View.VISIBLE);
            Book=findViewById(R.id.book_ticket_button);
            Bus_name=findViewById(R.id.bus_);
            Src_name=findViewById(R.id.src_name);
            Dest_name=findViewById(R.id.dest_name);
            this_layout=findViewById(R.id.ticket_window_layout);
            //Checking User
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            if(mAuth.getCurrentUser()==null) {
                Toast.makeText(this, "Login Token Expired", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Ticketing_window.this,LoginActivity.class));
                finish();
            }


            //Initialising the list
            mRef= FirebaseDatabase.getInstance().getReference("States");
            mListView=findViewById(R.id.ticketing_recycler);
            getUserData();
            prepareAdapter();
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Dest=stationlist.get(position);
                    //For Now We'll Set Source as Route Source
                    Src=stationlist.get(0);

                    Book.setText("Book Ticket for "+Dest.getLabel());
                }
            });



            Book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Src!=null&&Dest!=null)
                        prepareTicket(credentials,Src,Dest);
                    else
                        createCustomSnacbar("Please select destination",Ticketing_window.this,"ERROR").show();
                }
            });

             SetLocationTracker();
    }
    private Credentials getUserData() {

        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Credentials cred;
                 cred = dataSnapshot.getValue(Credentials.class);
                 prepareList(cred);
                 credentials =cred;
                } else {
                    createCustomSnacbar("Some Error Occurred",getApplicationContext(),"ERROR").show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout_user:
                CreateDialogueForLogOut();
                return true;

            case R.id.reverse_route:
                CreateDialogueForReversingRoute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }
    private void CreateDialogueForLogOut() {
        AlertDialog.Builder LogoutDialogue = new AlertDialog.Builder(this);
        LogoutDialogue.setTitle(getResources().getString(R.string.connfirm_logout));
        LogoutDialogue.setMessage(getResources().getString(R.string.logout_mesg));
        LogoutDialogue.setIcon(R.drawable.ic_error_outline_black_24dp);
        LogoutDialogue.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Successfully Logged Out",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Ticketing_window.this,LoginActivity.class));
                finish();
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
    private void CreateDialogueForReversingRoute()
    {
        AlertDialog.Builder LogoutDialogue = new AlertDialog.Builder(this);
        LogoutDialogue.setTitle(getResources().getString(R.string.connfirm_reverse));
        LogoutDialogue.setMessage(getResources().getString(R.string.reverse_mesg));
        LogoutDialogue.setIcon(R.drawable.ic_error_outline_black_24dp);
        LogoutDialogue.setPositiveButton("Reverse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collections.reverse(stationlist);
                Src_name.setText(stationlist.get(0).getLabel());
                Dest_name.setText(stationlist.get(stationlist.size()-1).getLabel());
                adapter.notifyDataSetChanged();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ticketing_window_menu, menu);



        return true;
    }
    //Preparing data for Ticket
    void prepareTicket(Credentials crd,final Station Source, Station Dest)
    {

        DatabaseReference ref=mRef;
        ref=ref.child(crd.getState()).child(crd.getCity()).child("Tickets");
        Date time= Calendar.getInstance().getTime();
        //Ticket Object Generated
        Ticket newTicket=new Ticket(crd.getBus_name(),Source,Dest,time,crd.getRoute_number());

        //Pushing data into Firebase
        ref.push().setValue(newTicket).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    createCustomSnacbar("Ticket Done Successfully",Ticketing_window.this,"SUCCESS").show();
                //Reset Source and Destination

            }
        });
        //Resetting the SRC & DEST
        Src=null;
        Dest=null;
    }
    Snackbar createCustomSnacbar(String text,Context context,String code)
    {// Create the Snackbar
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
    private void prepareList(Credentials cred) {
        credentials = cred;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("States").child(cred.getState()).child(cred.getCity()).child("Routes").child(cred.getRoute_number());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                //Traversing each node and retrieving the data
                for(DataSnapshot d:dataSnapshot.getChildren())
                    stationlist.add(dataSnapshot.child(d.getKey()).getValue(Station.class));
                //Progressbar Gone
                mBar.setVisibility(View.GONE);
                //Setting the SRC and DEST TextViews with respective station names
                Src_name.setText(stationlist.get(0).getLabel());
                Dest_name.setText(stationlist.get(stationlist.size()-1).getLabel());
                Bus_name.setText(credentials.getBus_number()+" | "+credentials.getBus_name());
                //Notifying Adapter about change in data
                adapter.notifyDataSetChanged();

                }
               // else
                   // Toast.makeText(getApplicationContext(),"Data is empty :"+YourCity+YourState+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(),"Some Error Occurred :"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void prepareAdapter() {
        //Creating adapter object and attaching it to the listView
        adapter=new TicketWindowAdapter(this,stationlist);
        mListView.setAdapter(adapter);
    }

void SetLocationTracker()
{
    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();



       } else
           {

            // Check location permission is granted - if it is, start
            // the service, otherwise request the permission
            int permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackerService();
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
                Toast.makeText(this, "Permission  Not Granted", Toast.LENGTH_SHORT).show();
            }
           }
}

private void startTrackerService() {

        startService(new Intent(this, TrackerService.class));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {

        }
    }


    //END OF MAIN CLASS
}


//Adapter class for Ticketing Window
class TicketWindowAdapter extends ArrayAdapter<Station> {
    ArrayList<Station> list;
    public TicketWindowAdapter(Context context, ArrayList<Station> words) {
        super(context, 0, words);
        list=words;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.ticket_window_list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Station conductor_view = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID miwok_text_view.
        TextView S_No = (TextView) listItemView.findViewById(R.id.s_no);
        // Get the Miwok translation from the conductor_view object and set this text on
        // the Miwok TextView.
        if(position==0)
        S_No.setText("SRC");
        else if(list.size()-1==position)
            S_No.setText("DEST");
        else
            S_No.setText(""+position);


        // Find the TextView in the list_item.xml layout with the ID default_text_view.
        TextView Station_Name = (TextView) listItemView.findViewById(R.id.station_name);
        // Get the default translation from the conductor_view object and set this text on
        // the default TextView.
        Station_Name.setText(conductor_view.getLabel());

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }


}