package com.example.secure_comm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DB {

    private static final String DBNAME="SMS_based_Encryption";
    private static final String TBUSER="usertb";
    private static final String TBFAB="fab";
    private static final int DBVERSION=1;

    private static final String UID="uid";
    private static final String UNAME="uname";
    private static final String UCONT="ucont";
    private static final String UEMAIl="uemail";
    private static final String UPASS="upass";
    private static final String UKEY="ukey";

    private static final String NAME="fabname";
    private static final String NO="fabno";
    private static final String KEY="fkey";

    SQLiteDatabase sqldb;
    dbhelper dbh;
    Context con;

    public class dbhelper extends SQLiteOpenHelper
    {

        public dbhelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+TBUSER+" (" + UID + " TEXT NOT NULL , " + UNAME + " TEXT NOT NULL ," +
                    UCONT+" TEXT NOT NULL, " + UEMAIl +" TEXT NOT NULL, " + UPASS + " TEXT NOT NULL , " + UKEY + " TEXT NOT NULL);");

            db.execSQL("create table "+TBFAB+" (" + UID + " TEXT NOT NULL , " + NAME + " TEXT NOT NULL ," +
                    NO + " TEXT NOT NULL , " + KEY + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TBUSER + "");
            db.execSQL("DROP TABLE IF EXISTS " + TBFAB + "");
        }
    }

    public DB(Context c)
    {
        con=c;
    }

    public DB open()
    {
        dbh=new dbhelper(con);
        sqldb=dbh.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbh.close();
    }

    public String uid()
    {
        String ans="1000";
        Cursor c=sqldb.query(TBUSER,new String[]{UID},null,null,null,null,UID + " DESC");
        if(c.getCount()>0)
        {
            c.moveToFirst();
            int i=Integer.parseInt(c.getString(0))+1;
            ans=""+i;
        }
        return ans;
    }

    public String register(String name,String mobile,String email,String pass,String key)
    {
        String ans="false";
        String id=uid();
        ContentValues cv=new ContentValues();
        cv.put(UID,id);
        cv.put(UNAME,name);
        cv.put(UCONT,mobile);
        cv.put(UEMAIl,email);
        cv.put(UPASS,pass);
        cv.put(UKEY,key);
        sqldb.insert(TBUSER,null,cv);
        ans="true";
        return ans;
    }


    public Boolean checkemail(String email)
    {
        Boolean ans=false;
        Cursor c=sqldb.query(TBUSER,null,UEMAIl +" = '" + email + "'",null,null,null,null);
        if(c.getCount()>0)
        {
            ans=true;
        }
        return ans;
    }

    public String login(String user,String pass)
    {
        String ans="false";
        Cursor c=sqldb.query(TBUSER,null,UEMAIl +" = '" + user + "' AND " + UPASS + " = '" + pass + "'",null,null,null,null);
        if(c.getCount()>0)
        {
            ans="true";
        }
        return ans;
    }


    public String getmykey(String email)
    {
        String ans="";
        Cursor c=sqldb.query(TBUSER,new String[]{UKEY},UEMAIl +" = '" + email + "'",null,null,null,null);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            ans=c.getString(0);
        }
        return ans;
    }

    public String addfav(String uid,String name,String no,String key)
    {
        String ans="false";
        ContentValues cv=new ContentValues();
        cv.put(UID,uid);
        cv.put(NAME,name);
        cv.put(NO,no);
        cv.put(KEY,key);
        sqldb.insert(TBFAB,null,cv);
        ans="true";
        return ans;
    }

    public String getfav(String email)
    {
        String ans="";
        Cursor c=sqldb.query(TBFAB,new String[]{NAME,NO},UID +" = '" + email + "'",null,null,null,null);
        if(c.getCount()>0)
        {
            while (c.moveToNext())
            ans+=c.getString(0)+"*"+c.getString(1)+"#";
        }
        else
        {
            ans="no";
        }
        return ans;
    }

    public void remove_fav(String email,String name,String no)
    {
        sqldb.delete(TBFAB,UID + " = '"+email+"' AND "+ NAME + " = '"+name+"' AND "+ NO + " = '"+no+"'",null);
    }

    public String getcont_Name(String uid,String cont)
    {
        String ans="no";
        Cursor c=sqldb.query(TBFAB,new String[]{NAME,NO,KEY},UID + " = '"+uid+"'",null,null,null,null);
        if(c.getCount()>0)
        {
            while (c.moveToNext())
            {
                if(cont.contains(c.getString(1)))
                {
                    ans=c.getString(0)+"*"+c.getString(2);
                }
            }
        }
        return ans;
    }


    public String getcont_Name_fornotification(String cont)
    {
        String ans="no";
        Cursor c=sqldb.query(TBFAB,new String[]{NAME,NO,KEY},null,null,null,null,null);
        if(c.getCount()>0)
        {
            while (c.moveToNext())
            {
                if(cont.contains(c.getString(1)))
                {
                    ans=c.getString(0);
                }
            }
        }
        return ans;
    }

}
