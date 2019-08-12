package com.example.secure_comm;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Add_Favorite extends AppCompatActivity{

    SharedPreferences sp;
    String uid="";
    EditText name,no,key;
    Button submit;
    ImageView addcont;

    String contname="",mobphone="",hphone="",wphone="",wmphone="",ophone="",mphone="";
    String[] contacts;
    String[] conttype=new String[]{"Mobile Number","Home Number","Work Number","Work Mobile Number","Other Number","Main Number"};

    String defno="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_favorites);
        sp=getSharedPreferences("sms_based_encryption", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        getSupportActionBar().setTitle("Add Favorites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        name= (EditText) findViewById(R.id.fab_name);
        no= (EditText) findViewById(R.id.fab_no);
        key= (EditText) findViewById(R.id.fab_key);
        submit= (Button) findViewById(R.id.add_fab);
        addcont= (ImageView) findViewById(R.id.fav_addcont);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().compareTo("")!=0 || no.getText().toString().compareTo("")!=0 || key.getText().toString().compareTo("")!=0)
                {
                    if(name.getText().toString().compareTo("")!=0)
                    {
                        if(no.getText().toString().compareTo("")!=0)
                        {
                            if(key.getText().toString().compareTo("")!=0)
                            {
                                if(key.length()==8)
                                {
                                    DB db=new DB(Add_Favorite.this);
                                    db.open();
                                    String ans=db.addfav(uid,name.getText().toString(),no.getText().toString(),key.getText().toString());
                                    db.close();
                                    if(ans.compareTo("true")==0)
                                    {
                                        Snackbar snack = Snackbar.make(v, "Contact Added!", Snackbar.LENGTH_SHORT);
                                        View vs = snack.getView();
                                        TextView txt = (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                                        txt.setTextColor(Color.GREEN);
                                        snack.show();
                                        name.setText("");
                                        key.setText("");
                                        no.setText("");
                                        name.requestFocus();
                                    }
                                }
                                else
                                {
                                    key.setError("Key Length Should be 8");
                                    key.requestFocus();
                                }
                            }
                            else
                            {
                                key.setError("Enter Key");
                                key.requestFocus();
                            }
                        }
                        else
                        {
                            no.setError("Enter Number");
                            no.requestFocus();
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
                    name.requestFocus();
                    Snackbar snack = Snackbar.make(v, "Fill all the Fields", Snackbar.LENGTH_SHORT);
                    View vs = snack.getView();
                    TextView txt = (TextView) vs.findViewById(android.support.design.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            }
        });

        addcont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!weHavePermissionToReadContacts())
                {
                    requestReadContactsPermissionFirst();
                }
                else
                {
                    addcont();
                }
            }
        });
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
        final Dialog d=new Dialog(Add_Favorite.this);
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
            Adapter adapt=new Adapter(Add_Favorite.this,data);
            list.setAdapter(adapt);
        }
        else
        {
            if(data.size()==1) {
                String temp[] = data.get(0).toString().split("\\*");
                name.setText(contname);
                no.setText(temp[1]);
            }
            else
            {
                if(defno.compareTo("")!=0) {
                    name.setText(contname);
                    no.setText(defno);
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
                name.setText(contname);
                no.setText(temp[1]);
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
}
