package com.example.secure_comm;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class Login extends AppCompatActivity{

    EditText user,pass;
    Button signin,signup;
    RelativeLayout ray;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("sms_based_encryption", Context.MODE_PRIVATE);
        String str=sp.getString("uid","");

        try{
            Intent i=getIntent();
            int nid=i.getIntExtra("nid",0);

            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel("abess",nid);
        }
        catch (Exception e){}

        if(str.compareTo("")!=0)
        {
            Intent i=new Intent(Login.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            setContentView(R.layout.login);
            user = (EditText) findViewById(R.id.user);
            pass = (EditText) findViewById(R.id.pass);
            signin = (Button) findViewById(R.id.signin);
            signup = (Button) findViewById(R.id.signup);
            ray = (RelativeLayout) findViewById(R.id.loginray);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (user.getText().toString().compareTo("") != 0 || pass.getText().toString().compareTo("") != 0)
                    {
                        if (user.getText().toString().compareTo("") != 0)
                        {
                            if(isEmailValid(user.getText().toString()))
                            {
                                if (pass.getText().toString().compareTo("") != 0)
                                {
                                    DB db=new DB(Login.this);
                                    db.open();
                                    String ans=db.login(user.getText().toString(),pass.getText().toString());
                                    db.close();

                                    if(ans.compareTo("true")==0)
                                    {
                                        editor=sp.edit();
                                        editor.putString("uid",user.getText().toString());
                                        editor.commit();

                                        Intent i=new Intent(Login.this,MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        user.setText("");
                                        pass.setText("");user.requestFocus();

                                        Snackbar snack = Snackbar.make(v, "Wrong Credentials", Snackbar.LENGTH_SHORT);
                                        View vs = snack.getView();
                                        TextView txt = (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                                        txt.setTextColor(Color.RED);
                                        snack.show();

                                    }
                                }
                                else
                                {
                                    pass.setError("Enter Password");
                                    pass.requestFocus();
                                }
                            }
                            else
                            {
                                user.setError("Invalid Email Format");
                                user.requestFocus();
                            }
                        }
                        else
                        {
                            user.setError("Enter Email");
                            user.requestFocus();
                        }
                    }
                    else
                    {
                        Snackbar snack = Snackbar.make(v, "Enter Email & Password to Proceed", Snackbar.LENGTH_SHORT);
                        View vs = snack.getView();
                        TextView txt = (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                        txt.setTextColor(Color.RED);
                        snack.show();
                    }
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Login.this, Register.class);
                    startActivity(i);
                }
            });
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
