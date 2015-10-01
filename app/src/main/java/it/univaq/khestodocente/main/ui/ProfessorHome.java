package it.univaq.khestodocente.main.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Url;
import it.univaq.khestodocente.main.ui.fragment.ChatFragment;
import it.univaq.khestodocente.main.ui.fragment.FilesFragment;
import it.univaq.khestodocente.main.ui.fragment.CoursesFragment;
import it.univaq.khestodocente.utils.SlidingTabLayout;

public class ProfessorHome extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    private OnRequestCallback mCallback1, mCallback2, mCallback3;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_home);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mSlidingTabLayout.setViewPager(mViewPager);

        new CoursesRequestTask().execute(Controller.getInstance().getUser().getId());


        //System.out.println("000Attributo arraylist nel main dopo il task: " + this.arraylistjsonobjcourse.toString());
        //System.out.println("Attributo utente nel main dopo il task: " + this.utente.toString());
        //System.out.println("Attributo map nel main dopo il task: " + this.idToNamecourse.toString());
    }

    private class CoursesRequestTask extends AsyncTask <Long, Void, ArrayList<JSONObject>> {
        @Override
        protected ArrayList<JSONObject> doInBackground(Long... params) {
            ArrayList<JSONObject> taskobject = new ArrayList<>();
            try {
                URL url = new Url().getStudentcoursesURL(params[0]);
                System.out.println("URL nel task dell' activity" + url);

                if(Controller.getInstance().isOnline(ProfessorHome.this))
                {
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestMethod("GET");

                    int code = urlc.getResponseCode();
                    System.out.println("RESPONSE CODE " + code);

                    InputStream is;
                    if (code == HttpURLConnection.HTTP_OK) {
                        is = urlc.getInputStream();
                    } else is = urlc.getErrorStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null){
                        sb.append(line);
                    }
                    br.close();

                    String risultato = sb.toString();

                    JSONObject jsonObj= new JSONObject(risultato);
                    System.out.println("jsonObjCorsi = " + jsonObj.toString());

                    Object dataObject = jsonObj.get("result");
                    if (dataObject instanceof JSONArray)
                    {
                        System.out.println("Ho ricevuto un JsonArray");

                        JSONArray dataJsonArray = (JSONArray) dataObject;
                        System.out.println("Il datajsonarray è questo: " + dataJsonArray.toString());
                        if (dataJsonArray != null)
                        {
                            for (int i= 0; i<dataJsonArray.length(); i++)
                            {
                                taskobject.add(dataJsonArray.getJSONObject(i));
                            }
                        }
                        else
                        {
                            System.out.println("JsonArray vuoto");
                        }
                        System.out.println("Variabile corsi: " + taskobject.toString());

                    }
                    else {
                        System.out.println("Non ho ricevuto un JsonArray");
                    }

                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("finito il doinbackground dell0 activity");
            return taskobject;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {
            Controller.getInstance().setArrlistJSONobjCourses(jsonObjects);
            if(mCallback1 != null) mCallback1.onComplete(jsonObjects);
            if(mCallback2 != null) mCallback2.onComplete(jsonObjects);
            if(mCallback3 != null) mCallback3.onComplete(jsonObjects);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_professor_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return CoursesFragment.newInstance(position + 1);
                case 1:
                    return FilesFragment.newInstance(position + 1);
                case 2:
                    return ChatFragment.newInstance(position + 1);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public interface OnRequestCallback {
        void onComplete(ArrayList<JSONObject> data);
    }

    public void setCallback(OnRequestCallback callback, int number){
        if(number == 1) mCallback1 = callback;
        if(number == 2) mCallback2 = callback;
        if(number == 3) mCallback3 = callback;
    }
}
