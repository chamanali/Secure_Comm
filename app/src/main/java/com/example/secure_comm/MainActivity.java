package com.example.secure_comm;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String uid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp=getSharedPreferences("sms_based_encryption",Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        if (!weHavePermissionToReadMessages())
        {
            requestReadMessagesPermissionFirst();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=new MenuInflater(MainActivity.this);
        mi.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.key)
        {
            show_key_dailog();
        }
        else if(item.getItemId()==R.id.logout)
        {
            editor=sp.edit();
            editor.putString("uid","");
            editor.commit();

            Intent i=new Intent(MainActivity.this,Login.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0 : return new Inbox();
                case 1 : return new Sent();
                case 2 : return new Favorite();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Inbox";
                case 1 :
                    return "Sent";
                case 2 :
                    return "Favorites";
            }
            return null;
        }
    }

    private boolean weHavePermissionToReadMessages()
    {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestReadMessagesPermissionFirst()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            requestForResultMessagesPermission();
        } else {
            requestForResultMessagesPermission();
        }
    }
    private void requestForResultMessagesPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, 222);
    }

    public void show_key_dailog()
    {
        Dialog d=new Dialog(MainActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.key_dailog);
        final TextView keytext= (TextView) d.findViewById(R.id.textkey);
        ImageView copy= (ImageView) d.findViewById(R.id.copykey);

        DB db=new DB(MainActivity.this);
        db.open();
        keytext.setText(db.getmykey(uid));
        db.close();

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(keytext.getText().toString());
                Toast.makeText(MainActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        d.show();
    }
}
