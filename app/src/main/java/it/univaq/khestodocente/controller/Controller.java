package it.univaq.khestodocente.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.univaq.khestodocente.main.Model.User;

/**
 * Created by beniamino on 22/09/15.
 */
public class Controller {

    private static Controller instance = null;

    private User mUser;

    private List<Map<String, Object>> mListcorses;

    private ArrayList<JSONObject> arrlistJSONobjCourses;


    private HashMap<String,ArrayList<JSONObject>> mapCourseSections;



    public static Controller getInstance(){
        if(instance == null) instance = new Controller();
        return instance;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public List<Map<String, Object>> getmListcorses() {
        return mListcorses;
    }

    public void setmListcorses(List<Map<String, Object>> mListcorses) {
        this.mListcorses = mListcorses;
    }

    public ArrayList<JSONObject> getArrlistJSONobjCourses() {
        return arrlistJSONobjCourses;
    }

    public void setArrlistJSONobjCourses(ArrayList<JSONObject> arrlistJSONobjCourses) {
        this.arrlistJSONobjCourses = arrlistJSONobjCourses;
    }

    public HashMap<String, ArrayList<JSONObject>> getMapCourseSections() {
        return mapCourseSections;
    }

    public ArrayList<JSONObject> getCourseSections (String idcorso) {
        return mapCourseSections.get(idcorso);
    }

    public void addCourseSections(String idcorso, ArrayList<JSONObject> newSections) {
        if (this.mapCourseSections == null)
        {
            this.mapCourseSections= new HashMap<>();
        }
        this.mapCourseSections.put(idcorso,newSections);
    }

    public String parseMyDate (long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ITALY);
        calendar.setTimeInMillis(timestamp);
        String day = ((Integer)calendar.get(Calendar.DAY_OF_MONTH)).toString();
        int monthminusone = calendar.get(Calendar.MONTH);
        Integer integermonth = monthminusone +1;
        String month = integermonth.toString();
        String year = ((Integer)calendar.get(Calendar.YEAR)).toString();

        Integer integerHour = calendar.get(Calendar.HOUR);
        String hour = integerHour.toString();
        if (integerHour<10)
        {
            hour = "0" + hour;
        }

        Integer integerMinute = calendar.get(Calendar.MINUTE);
        String minute = integerMinute.toString();
        if (integerMinute<10)
        {
            minute = "0" + minute;
        }

        String date = day + "-" + month + "-" + year + "  " + hour + ":" + minute;
        return date;
    }


    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
