package com.example.secure_comm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hp on 3/6/2017.
 */

public class Favorite extends Fragment{

    ListView list;
    ArrayList<String> data;
    FloatingActionButton addnew;

    SharedPreferences sp;
    String uid="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.favorite,container,false);
        sp=getActivity().getSharedPreferences("sms_based_encryption", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        list= (ListView) v.findViewById(R.id.fav_list);
        addnew= (FloatingActionButton) v.findViewById(R.id.newfab);

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),Add_Favorite.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getdata();

    }

    public void getdata()
    {
        DB db=new DB(getActivity());
        db.open();
        String ans=db.getfav(uid);
        db.close();

        if(ans.compareTo("no")==0)
        {
            list.setAdapter(null);
            Snackbar.make(list,"NO Contacts have added as Favorites!",Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            String temp[]=ans.split("\\#");
            data=new ArrayList<String>();
            for(int i=0;i<temp.length;i++)
            {
                data.add(temp[i]);
            }

            Adapter adapt=new Adapter(getActivity(),data);
            list.setAdapter(adapt);
        }

    }

    public class Adapter extends ArrayAdapter<String>
    {
        Context con;
        ArrayList<String> dataset;
        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.fav_list_item,data);
            con=context;
            dataset=data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater li=LayoutInflater.from(con);
            View v=li.inflate(R.layout.fav_list_item, null, true);
            TextView name= (TextView) v.findViewById(R.id.favli_name);
            TextView no= (TextView) v.findViewById(R.id.favli_no);

            final String temp[]=dataset.get(position).split("\\*");
            name.setText(temp[0]);
            no.setText(temp[1]);

            ImageView remove= (ImageView) v.findViewById(R.id.favli_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DB db=new DB(con);
                    db.open();
                    db.remove_fav(uid,temp[0],temp[1]);
                    db.close();
                    getdata();
                }
            });
            return v;
        }
    }
}
