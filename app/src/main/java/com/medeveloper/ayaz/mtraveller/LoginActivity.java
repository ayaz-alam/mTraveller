package com.medeveloper.ayaz.mtraveller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText busNumber,pass;
    private String Username,Password;
    private Button   login_button;
    private TextView register;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                busNumber = findViewById(R.id.bus_number_login);
                pass = findViewById(R.id.password_login);
                login_button = findViewById(R.id.login_button);
                register = findViewById(R.id.login_register);
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SignUp.class));}
                });
                login_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!isConnected(LoginActivity.this))buildDialog(LoginActivity.this).show();
                                Username = busNumber.getText().toString();
                                Password = pass.getText().toString();
                                if (check(Username, Password))
                                {
                                    mAuth.signInWithEmailAndPassword(Username,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful())
                                            {
                                                Intent i = new Intent(LoginActivity.this, Ticketing_window.class);
                                                startActivity(i);
                                            }
                                            else {
                                                Toast.makeText(getBaseContext(), "Cannot Log you in", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }



                        }
                    });

            }




    private boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile!=null&&mobile.isConnectedOrConnecting())||(wifi!=null&&wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(Context c){
        final AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setIcon(R.drawable.ic_error_outline_black_24dp);
        builder.setTitle(getString(R.string.nonet));
        builder.setMessage(getString(R.string.nonet_msg));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.Retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!isConnected(LoginActivity.this))
                    builder.show();
            }
        });

        return builder;
    }

    private boolean check(String username, String password) {
       busNumber.setError(null);
       pass.setError(null);
        boolean isOkay=true;
        if(username.equals(""))
        {
            busNumber.setError("Required Field");
            busNumber.requestFocus();
            isOkay=false;
        }
        else if(username.length()<5)
        {
            busNumber.setError("Invalid Username");
            busNumber.requestFocus();
            isOkay=false;

        }
        else if(pass.equals(""))
        {
            pass.setError("Required Field");
            pass.requestFocus();
            isOkay=false;
        }
        else if(pass.length()<6)
        {
            pass.setError("Password length must be 6 digit");
            pass.requestFocus();


            isOkay=false;
        }
        else if(pass.equals("123456"))
        {
            pass.setError("Password must be complicated");
            pass.requestFocus();

            isOkay=false;
        }

        return isOkay;
    }

}
