package com.medeveloper.ayaz.mtraveller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {


    EditText Email,Password;
    Button   RegisterButton;
    TextView LoginButton;
    FirebaseAuth mAuth;
    RelativeLayout this_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.email_login);
        Password = findViewById(R.id.password_login);
        LoginButton = findViewById(R.id.login_register);
        RegisterButton = findViewById(R.id.signup_button);
        this_layout = findViewById(R.id.signup_layout);
        mAuth = FirebaseAuth.getInstance();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check())
                {
                    CreateUser();
                }
            }
        });


    }

    private void CreateUser() {
        mAuth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    startActivity(new Intent(getApplicationContext(),Registration_Form.class));
            }
        });
    }

    private boolean check() {
        boolean isOkay=true;
        if(isConnected(getApplicationContext())) {
            String email = Email.getText().toString();
            String pass = Password.getText().toString();
            if (email.equals("")) {
                Email.setError("Required");
                Email.requestFocus();
                isOkay = false;
            } else if (pass.equals("")) {
                Password.setError("Required");
                Password.requestFocus();
                isOkay = false;
            }
        }
        else {
            createCustomSnackbar("No Internet Connection", getApplicationContext(), "ERROR").show();
            isOkay =false;
        }
            return isOkay;

    }

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


    public boolean isConnected(Context context) {
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

}
