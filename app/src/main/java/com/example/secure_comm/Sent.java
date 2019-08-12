package com.example.secure_comm;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Sent extends Fragment{

    ArrayList<String> id,name,date,body;
    Date da,dn;
    SimpleDateFormat sdfd=new SimpleDateFormat("MMM d");
    SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
    ListView list;

    SharedPreferences sp;
    String uid="";
    String mykey="";

    Timer timer;
    TimerTask task;
    Handler hand=new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.sent,container,false);
        list= (ListView) v.findViewById(R.id.sentlist);
        sp=getActivity().getSharedPreferences("sms_based_encryption", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        getmykey();

        timer=new Timer();
        init_timer();
        timer.schedule(task,0,5000);

        return v;
    }



    private boolean weHavePermissionToReadContacts()
    {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermissionFirst()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS)) {
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 123);
    }


    public class adapt extends ArrayAdapter<String>
    {
        Context con;
        ArrayList<String> id,name,date,body;
        public adapt(Context context, ArrayList<String> id,ArrayList<String> name,ArrayList<String> body,ArrayList<String> date) {
            super(context, R.layout.message_activity_list_item,id);
            con=context;
            this.id=id;
            this.name=name;
            this.date=date;
            this.body=body;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater li=LayoutInflater.from(con);
            View v=li.inflate(R.layout.message_activity_list_item, null, true);
            RelativeLayout ray= (RelativeLayout) v.findViewById(R.id.liray);
            TextView n= (TextView) v.findViewById(R.id.name);
            TextView b= (TextView) v.findViewById(R.id.body);
            TextView dt= (TextView) v.findViewById(R.id.datetime);
            dn=new Date();
            String datenow=sdfd.format(dn);

            da=new Date(Long.parseLong(date.get(position)));
            String mdate=sdfd.format(da);
            String mtime=sdft.format(da);
            if(datenow.compareTo(mdate)==0)
            {
                dt.setText(mtime);
            }
            else
            {
                dt.setText(mdate);
            }

            DB db=new DB(con);
            db.open();
            String ans=db.getcont_Name(uid,name.get(position));
            db.close();

            if(ans.compareTo("no")==0)
            {
                n.setText(name.get(position));
            }
            else
            {
                String temp1[]=ans.split("\\*");
                n.setText(temp1[0]);
            }

            Authentication a=new Authentication();

            byte[] txt1= new byte[0];
            try {
                String removeprefix=body.get(position).substring(6,body.get(position).length());
                txt1 = a.decrypt(removeprefix,mykey);
                String text1 = new String(txt1, "UTF-8");
                b.setText(text1);
            } catch (Exception e) {
                b.setText(body.get(position));
//                Toast.makeText(getActivity(), "d-"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            ray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog d=new Dialog(con);
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.message_activity_dailog);
                    TextView dname= (TextView) d.findViewById(R.id.dname);
                    TextView ddt= (TextView) d.findViewById(R.id.ddt);
                    ImageView copy= (ImageView) d.findViewById(R.id.dcopy);
                    final TextView dbody= (TextView) d.findViewById(R.id.dbody);

                    String n="";
                    DB db=new DB(con);
                    db.open();
                    String ans=db.getcont_Name(uid,name.get(position));
                    db.close();

                    if(ans.compareTo("no")==0)
                    {
                        n="<b>Sender: </b>"+name.get(position);
                    }
                    else
                    {
                        String temp1[]=ans.split("\\*");
                        n="<b>Sender: </b>"+temp1[0];
                    }

                    String b="";
                    Authentication a=new Authentication();

                    byte[] txt1= new byte[0];
                    try {
                        String removeprefix=body.get(position).substring(6,body.get(position).length());
                        txt1 = a.decrypt(removeprefix,mykey);
                        String text1 = new String(txt1, "UTF-8");
                        b="<b>Message: </b>"+text1;
                    } catch (Exception e) {
                        b="<b>Message: </b>"+body.get(position);
//                        Toast.makeText(getActivity(), "d-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    da=new Date(Long.parseLong(date.get(position)));
                    String dt="<b>Date Time: </b>"+sdf.format(da);

                    dname.setText(Html.fromHtml(n));
                    ddt.setText(Html.fromHtml(dt));
                    dbody.setText(Html.fromHtml(b));

                    copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) con.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(dbody.getText().toString());
                            Toast.makeText(con,"Copied to Clipboard",Toast.LENGTH_SHORT).show();
                        }
                    });
                    d.show();
                }
            });

            return v;
        }
    }

    public void getmykey()
    {
        DB db=new DB(getActivity());
        db.open();
        String temp=db.getmykey(uid);
        db.close();
        mykey=temp+temp;
    }


    public void init_timer()
    {
        task=new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        getdata();
                    }
                });
            }
        };
    }

    public void getdata()
    {
        if(weHavePermissionToReadContacts())
        {
            Uri uriSMSURI = Uri.parse("content://sms/sent");
            Cursor cur = getActivity().getContentResolver().query(uriSMSURI, new String[]{"_id", "address", "date", "body"}, null, null,null);
            String sms = "";
            id=new ArrayList<String>();
            name=new ArrayList<String>();
            date=new ArrayList<String>();
            body=new ArrayList<String>();

            while (cur.moveToNext())
            {
                if(cur.getString(3).contains("ABESS-"))
                {
                    id.add(cur.getString(0));
                    name.add(cur.getString(1));
                    date.add(cur.getString(2));
                    body.add(cur.getString(3));
                }
            }

            adapt ad=new adapt(getActivity(),id,name,body,date);
            list.setAdapter(ad);
        }
        else
        {
            requestReadContactsPermissionFirst();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            timer.cancel();}
        catch (Exception e){}
    }
}
