package com.example.secure_comm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hp on 1/6/2017.
 */

public class Register extends AppCompatActivity{
    RelativeLayout ray;
    EditText name,cont,email,pass,cpass;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Register");

        ray= (RelativeLayout) findViewById(R.id.ray);
        name= (EditText) findViewById(R.id.fullname);
        cont= (EditText) findViewById(R.id.cont);
        email= (EditText) findViewById(R.id.email);
        pass= (EditText) findViewById(R.id.pass);
        cpass= (EditText) findViewById(R.id.cpass);

        submit= (Button) findViewById(R.id.register);

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(name.getText().toString().compareTo("")!=0 || cont.getText().toString().compareTo("")!=0 || email.getText().toString().compareTo("")!=0 || pass.getText().toString().compareTo("")!=0 || cpass.getText().toString().compareTo("")!=0)
                {
                    if(name.getText().toString().compareTo("")!=0)
                    {
                        if(cont.getText().toString().compareTo("")!=0)
                            {
                                if(email.getText().toString().compareTo("")!=0)
                                {
                                    if(isEmailValid(email.getText().toString()))
                                    {
                                        if(pass.getText().toString().compareTo("")!=0)
                                        {
                                            if(cpass.getText().toString().compareTo("")!=0)
                                            {
                                                if(cpass.getText().toString().compareTo(pass.getText().toString())==0)
                                                {
                                                    if(check_email())
                                                    {
                                                        Snackbar snack=Snackbar.make(v,"Email already Exists!",Snackbar.LENGTH_SHORT);
                                                        View vs=snack.getView();
                                                        TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                                                        txt.setTextColor(Color.RED);
                                                        snack.show();
                                                        email.requestFocus();
                                                    }
                                                    else
                                                    {
                                                        DB db=new DB(Register.this);
                                                        db.open();
                                                        String ans=db.register(name.getText().toString(),cont.getText().toString(),email.getText().toString(),pass.getText().toString(),generate_my_key());
                                                        db.close();

                                                        if(ans.compareTo("true")==0)
                                                        {
                                                            Toast.makeText(Register.this, "User Registered", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    pass.setText("");
                                                    cpass.setText("");
                                                    pass.requestFocus();
                                                    Snackbar snack=Snackbar.make(v,"Passwords dont Match!",Snackbar.LENGTH_SHORT);
                                                    View vs=snack.getView();
                                                    TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                                                    txt.setTextColor(Color.RED);
                                                    snack.show();
                                                }
                                            }
                                            else
                                            {
                                                cpass.setError("Confirm your Password");
                                                cpass.requestFocus();
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
                                        email.setError("Invalid Email Format");
                                        email.requestFocus();
                                    }
                                }
                                else
                                {
                                    email.setError("Enter Email");
                                    email.requestFocus();
                                }
                            }
                            else
                            {
                                cont.setError("Enter Contact Number");
                                cont.requestFocus();
                            }
                    }
                    else
                    {
                        name.setError("Enter Name");
                        name.requestFocus();
                    }
                }
                else
                {
                    Snackbar snack=Snackbar.make(v,"Fill all the Fields",Snackbar.LENGTH_SHORT);
                    View vs=snack.getView();
                    TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String generate_my_key()
    {
        String ans="";
        int no[]=new int[]{0,1,2,3,4,5,6,7,8,9};
        String[] alpha=new String[]{"Q","W","E","R","T","Y","U","I","O","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M"};
        Random rand=new Random();
        ans+=alpha[rand.nextInt(alpha.length)];
        ans+=no[rand.nextInt(no.length)];
        ans+=alpha[rand.nextInt(alpha.length)];
        ans+=no[rand.nextInt(no.length)];
        ans+=alpha[rand.nextInt(alpha.length)];
        ans+=no[rand.nextInt(no.length)];
        ans+=alpha[rand.nextInt(alpha.length)];
        ans+=no[rand.nextInt(no.length)];
        return ans;
    }

    public Boolean check_email()
    {
        Boolean ans=true;
        DB db=new DB(Register.this);
        db.open();
        ans=db.checkemail(email.getText().toString());
        db.close();
        return ans;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
