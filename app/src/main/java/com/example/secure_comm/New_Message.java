package com.example.secure_comm;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class New_Message extends AppCompatActivity{

    EditText recp,mesg;
    ImageView cont;
    Button send;

    String contname="",mobphone="",hphone="",wphone="",wmphone="",ophone="",mphone="";
    String[] contacts;
    String[] conttype=new String[]{"Mobile Number","Home Number","Work Number","Work Mobile Number","Other Number","Main Number"};

    String defno="";
    SharedPreferences sp;
    String uid="";
    String prefix="ABESS-";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_message);
        getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recp= (EditText) findViewById(R.id.recp);
        mesg= (EditText) findViewById(R.id.mesg);
        cont= (ImageView) findViewById(R.id.select_cont);
        send= (Button) findViewById(R.id.send);
        sp=getSharedPreferences("sms_based_encryption",Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        if(!weHavePermissionToReadContacts())
        {
            requestReadContactsPermissionFirst();
        }

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!weHavePermissionToReadContacts())
                {
                    requestReadContactsPermissionFirst();
                }
                else
                {
                    DB db=new DB(New_Message.this);
                    db.open();
                    String ans=db.getfav(uid);
                    db.close();

                    if(ans.compareTo("no")==0)
                    {
                        addcont();
                    }
                    else
                    {
                        dailog_conatct();
                    }
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(recp.getText().toString().compareTo("")!=0 || mesg.getText().toString().compareTo("")!=0)
                {
                    if(recp.getText().toString().compareTo("")!=0)
                    {
                        if(mesg.getText().toString().compareTo("")!=0)
                        {
                            String temp[]=recp.getText().toString().split(",");
                            for(int i=0;i<temp.length;i++)
                            {
                                String encrypted_mesg="";
                                DB db=new DB(New_Message.this);
                                db.open();
                                String key=db.getmykey(uid)+db.getmykey(uid);
                                db.close();

                                Authentication a=new Authentication();
                                byte[] txt0 = new byte[0];
                                try
                                {
                                    txt0 = a.encrypt(mesg.getText().toString(),key);
                                    encrypted_mesg = prefix+Base64.encodeToString(txt0,Base64.DEFAULT);
                                }
                                catch (Exception e)
                                {
//                                    Toast.makeText(New_Message.this,"e-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                try
                                {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(temp[i],null,encrypted_mesg, null, null);
                                }catch (Exception e)
                                {
                                    Toast.makeText(New_Message.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                if(temp.length>1)
                                {
                                    if (i < temp.length - 1)
                                    {
                                        Toast.makeText(New_Message.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(New_Message.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                        else
                        {
                            Snackbar snack=Snackbar.make(v,"Enter Message",Snackbar.LENGTH_SHORT);
                            View vs=snack.getView();
                            TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                            txt.setTextColor(Color.RED);
                            snack.show();
                            recp.requestFocus();
                        }
                    }
                    else
                    {
                        Snackbar snack=Snackbar.make(v,"Enter Recipient",Snackbar.LENGTH_SHORT);
                        View vs=snack.getView();
                        TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                        txt.setTextColor(Color.RED);
                        snack.show();
                        recp.requestFocus();
                    }
                }
                else
                {
                    Snackbar snack=Snackbar.make(v,"Enter Recipients & Message",Snackbar.LENGTH_SHORT);
                    View vs=snack.getView();
                    TextView txt= (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                    recp.requestFocus();
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


    private boolean weHavePermissionToReadContacts()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestReadContactsPermissionFirst()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }
    private void requestForResultContactsPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 111);
    }

    public void addcont()
    {
        Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, 111);
    }

    public void append(String s)
    {
        if(recp.length()>0)
        {
            String r=recp.getText().toString();
            recp.setText(r+","+s.trim());
            recp.setSelection(recp.length());
        }
        else
        {
            recp.setText(s.trim());
            recp.setSelection(recp.length());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 111)
        {
            Uri returnUri = data.getData();
            Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);
            Integer contactsCount = cursor.getCount();
            if (contactsCount > 0)
            {
                while(cursor.moveToNext())
                {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    contname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        Cursor pCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCursor.moveToNext())
                        {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            defno=phoneNo;
                            switch (phoneType)
                            {
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    mobphone=phoneNo;
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    hphone=phoneNo;
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    wphone=phoneNo;
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                    wmphone=phoneNo;
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                    ophone=phoneNo;
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                    mphone=phoneNo;
                                    break;
                                default:
                                    break;
                            }

                        }
                        contacts=new String[]{mobphone,hphone,wphone,wmphone,ophone,mphone};
                        cont_Selection();
                        pCursor.close();

                    }
                    else
                    {
                        Toast.makeText(this, "There is no Contact Number associated with the Contact Person", Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
            }
            else
            {
                Toast.makeText(this, "There is no Contact Details associated with the Contact Person", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cont_Selection()
    {
        final Dialog d=new Dialog(New_Message.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.cont_selection);
        ListView list= (ListView) d.findViewById(R.id.contlist);

        final ArrayList<String> data=new ArrayList<String>();
        for(int i=0;i<contacts.length;i++)
        {
            if(contacts[i].compareTo("")!=0)
            {
                data.add(conttype[i]+"*"+contacts[i]);
            }
        }

        if(data.size()>1)
        {
            Adapter adapt=new Adapter(New_Message.this,data);
            list.setAdapter(adapt);
        }
        else
        {
            if(data.size()==1) {
                String temp[] = data.get(0).toString().split("\\*");
                append(temp[1]);
            }
            else
            {
                if(defno.compareTo("")!=0) {
                    append(defno);
                }
                else
                {
                    Toast.makeText(this, "There is no Contact Details associated with the Contact Person", Toast.LENGTH_SHORT).show();
                }
            }
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp[]=data.get(position).toString().split("\\*");
                append(temp[1]);
                d.cancel();
            }
        });

        if(data.size()>1) {
            d.show();
        }
        contname="";mobphone="";hphone="";wphone="";wmphone="";ophone="";mphone="";defno="";
    }


    public class Adapter extends ArrayAdapter<String>
    {
        Context con;
        ArrayList<String> dataset;
        public Adapter(Context context,ArrayList<String> data) {
            super(context, R.layout.list_item,data);
            con=context;
            dataset=data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.list_item,null,true);
            TextView type= (TextView) v.findViewById(R.id.type);
            TextView no= (TextView) v.findViewById(R.id.no);

            String temp[]=dataset.get(position).split("\\*");
            type.setText(temp[0]);
            no.setText(temp[1]);
            return v;

        }
    }


    public void dailog_conatct()
    {
        final Dialog d=new Dialog(New_Message.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.contact_dailog);
        LinearLayout tab_cont,tab_fav;
        tab_cont= (LinearLayout) d.findViewById(R.id.d_cont);
        tab_fav= (LinearLayout) d.findViewById(R.id.d_fav);

        tab_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                addcont();
            }
        });

        tab_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                DB db=new DB(New_Message.this);
                db.open();
                String ans=db.getfav(uid);
                db.close();

                final ArrayList<String> data=new ArrayList<String>();
                String temp[]=ans.split("\\#");
                for(int i=0;i<temp.length;i++)
                {
                    data.add(temp[i]);
                }
                final Dialog d_c=new Dialog(New_Message.this);
                d_c.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d_c.setContentView(R.layout.cont_selection);
                ListView list= (ListView) d_c.findViewById(R.id.contlist);

                Adapter adapt=new Adapter(New_Message.this,data);
                list.setAdapter(adapt);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String temp[]=data.get(position).toString().split("\\*");
                        append(temp[1]);
                        d_c.cancel();
                    }
                });

                d_c.show();
            }
        });
        d.show();
    }
}
