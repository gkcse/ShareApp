package com.anu.developers3k.shareapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.anu.developers3k.shareapp.adapter.AppsManager;
import com.anu.developers3k.shareapp.adapter.DeviceInfoAdapter;
import com.anu.developers3k.shareapp.adapter.DeviceInfoManager;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by i321994 on 25/10/17.
 */

public class AppInfoClass extends AppCompatActivity {
    PackageManager pm = null;
    private static int SPLASH_TIME_OUT = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdetaillayout);

        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.transitions_container);

        Intent intent = getIntent();
        String packageName = intent.getStringExtra("packagename");

        AppsManager appsManager = new AppsManager(getApplicationContext());
        // Get the current app label
        String label = appsManager.getApplicationLabelByPackageName(packageName);

        //app name printing
        TextView appname = (TextView)findViewById(R.id.textView);
        appname.setText(label);

        //app icon
        ImageView appnameicon = (ImageView)findViewById(R.id.imageView);
        appnameicon.setImageDrawable(appsManager.getAppIconByPackageName(packageName));


        //first install time
        final TextView firstInstallTime = (TextView)findViewById(R.id.text_versionname1);
        firstInstallTime.setText((appsManager.installTimeFromPackageManager(packageName).toString()));

        //update date time
        final TextView updateTime = (TextView)findViewById(R.id.text_versioncode1);
        updateTime.setText((appsManager.lastUpdateTimeFromPackageManager(packageName)).toString());

        String versionName = "";
        int versionCode = 0;
        try {
            pm = getPackageManager();
            versionName = pm.getPackageInfo(packageName, 0).versionName;
            versionCode = pm.getPackageInfo(packageName, 0).versionCode;
        }catch(Exception e){
            System.out.print("inside the error block");
        }
        //version name
        final TextView versionNametextView = (TextView)findViewById(R.id.text_versionname);
        versionNametextView.setText(versionName);

        //version code
        final TextView versionCodeTextView = (TextView)findViewById(R.id.text_versioncode);
        versionCodeTextView.setText(Integer.toString(versionCode));


        //animation initilization
        TransitionManager.beginDelayedTransition(transitionsContainer);

        //layout id
        final LinearLayout layoutDetail = (LinearLayout)findViewById(R.id.linearid);
        layoutDetail.setVisibility(View.GONE);


        //fetching the permission list
        appsManager.fetchDetail(packageName);

        //granted permission list
        ArrayList<String> grantedList=new ArrayList<String>();//Creating arraylist
        //denied permission list
        ArrayList<String> deniedList=new ArrayList<String>();//Creating arraylist
        grantedList = appsManager.fetchDetail(packageName).grantedPermissionList;
        deniedList = appsManager.fetchDetail(packageName).deniedPermissionList;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                boolean visible=true;
                TransitionSet set = new TransitionSet()
                        .addTransition(new Scale(0.7f))
                        .addTransition(new Fade())
                        .setInterpolator(visible ? new LinearOutSlowInInterpolator() :
                                new FastOutLinearInInterpolator());
                TransitionManager.beginDelayedTransition(transitionsContainer, set);
                layoutDetail.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            }
        }, SPLASH_TIME_OUT);


        //grantedList.
        HashMap<String, String> hmap = new HashMap<String, String>();

        for(int i=0;i<grantedList.size();i++){
            hmap.put(grantedList.get(i),"Allowed");
        }
        for(int i=0;i<deniedList.size();i++){
            hmap.put(deniedList.get(i),"Denied");
        }

        //on click on list view
        ListView lst = (ListView) findViewById(R.id.device_list);
        DeviceInfoAdapter adapter = new DeviceInfoAdapter(getApplicationContext(), R.layout.deviceinfolayout);
        lst.setAdapter(adapter);

         /* Display content using Iterator*/
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            DeviceInfoManager obj = new DeviceInfoManager(mentry.getKey().toString(), mentry.getValue().toString());
            adapter.add(obj);
        }
        //no permisison case
        if(hmap.size()==0){
            TextView txtmsg = (TextView)findViewById(R.id.permission_msg);
            txtmsg.setVisibility(View.VISIBLE);
        }
//        lst.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    return true; // Indicates that this has been handled by you and will not be forwarded further.
//                }
//                return false;
//            }
//        });

    }
}
