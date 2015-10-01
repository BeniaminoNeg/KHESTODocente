package it.univaq.khestodocente.main.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Url;

public class VCourse extends AppCompatActivity {

    private String idCorso;

    public String getIdCorso() {
        return idCorso;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        idCorso = b.getString("Id");
        setContentView(R.layout.activity_course);

        ArrayList<JSONObject> corsi = Controller.getInstance().getArrlistJSONobjCourses();
        String fullnamecorso= "";
        boolean trovato = false;
        for (int i=0; i<corsi.size() && !trovato; i++)
        {
            if (corsi.get(i).optString("id").equals(idCorso))
            {
                trovato = true;
                fullnamecorso = corsi.get(i).optString("fullname");
            }
        }
        if (!fullnamecorso.equals(""))
        {
            setTitle(fullnamecorso);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vcourse, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class VCourseFragment extends Fragment {

        private ListView listview;

        private HashMap<String,Boolean> sectionHasFile;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_course, container, false);
            listview= (ListView) root.findViewById(R.id.course_listView_listsections);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String idsection = ((JSONObject) listview.getAdapter().getItem(i)).optString("id");
                    String sectionnumber = ((JSONObject) listview.getAdapter().getItem(i)).optString("section");

                    System.out.println("HAI CLICCATO SULLA SECTION CON ID " + idsection + " E SECTION NUMBER " + sectionnumber);
                    Intent intent = new Intent(getActivity(),VSection.class);
                    intent.putExtra("idcourse",((VCourse)getActivity()).idCorso);
                    intent.putExtra("idsection",idsection);
                    getActivity().startActivity(intent);
                }
            });
            String idCorso = ((VCourse)this.getActivity()).getIdCorso();
            new CourseSectionsTask().execute(idCorso);


            return root;
        }

        private class CourseSectionsTask extends AsyncTask <String, Void, ArrayList<JSONObject>> {
            @Override
            protected ArrayList<JSONObject> doInBackground(String... strings) {
                ArrayList<JSONObject> taskobject = new ArrayList<JSONObject>();
                try {
                    URL url = new Url().getCourseSectionURL(strings[0]);
                    System.out.println(url);
                    if (Controller.getInstance().isOnline(getContext()))
                    {
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setRequestMethod("GET");
                        int code = urlc.getResponseCode();
                        System.out.println("RESPONSE CODE " + code);
                        InputStream is;
                        if(code == HttpURLConnection.HTTP_OK) {
                            is = urlc.getInputStream();
                        } else is = urlc.getErrorStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line = br.readLine()) != null){
                            sb.append(line);
                        }
                        br.close();
                        String risultato = sb.toString();
                        JSONObject jsonObj = new JSONObject(risultato);
                        Object dataObject = jsonObj.get("result");
                        //System.out.println("ClassName= "+dataObject.getClass().getName());
                        if (dataObject instanceof JSONArray)
                        {
                            JSONArray dataJsonArray = (JSONArray) dataObject;
                            if (dataJsonArray != null)
                            {
                                for (int i=0; i<dataJsonArray.length(); i++)
                                {
                                    taskobject.add(dataJsonArray.getJSONObject(i));
                                }
                            }
                        }
                    }
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("taskobject: " + taskobject.toString());
                return taskobject;
            }

            @Override
             protected void onPostExecute(ArrayList<JSONObject> list) {
                String idcorso = ((VCourse)getActivity()).idCorso;
                Controller.getInstance().addCourseSections(idcorso,list);
                sectionHasFile = new HashMap<>();
                for (int i=0; i<list.size(); i++)
                {
                    String currentSection = list.get(i).optString("id");
                    sectionHasFile.put(currentSection,false);
                }
                System.out.println("MAP HAS FILE APPENA CREATA " + sectionHasFile.toString());

                new CourseElementsTask().execute(idcorso);


            }



        }

        private class CourseElementsTask extends AsyncTask <String, Void, ArrayList<JSONObject>> {
            @Override
            protected ArrayList<JSONObject> doInBackground(String... params) {
                System.out.println("Sono nel task di info corsi");
                ArrayList<JSONObject> taskobject = new ArrayList<>();
                try {
                    URL url = new Url().getFilescourseURL(params[0]);
                    System.out.println(url);

                    if (isOnline())
                    {
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setRequestMethod("GET");

                        int code = urlc.getResponseCode();
                        System.out.println("RESPONSE CODE " + code);

                        InputStream is;
                        if(code == HttpURLConnection.HTTP_OK) {
                            is = urlc.getInputStream();
                        } else is = urlc.getErrorStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line = br.readLine()) != null){
                            sb.append(line);
                        }
                        br.close();
                        String risultato = sb.toString();

                        JSONObject jsonObj = new JSONObject(risultato);
                        System.out.println("jsonObj elementi singolo corso = " + jsonObj.toString());

                        Object dataObject = jsonObj.get("result");
                        //System.out.println("ClassName= "+dataObject.getClass().getName());
                        if (dataObject instanceof JSONArray)
                        {
                            System.out.println("Il materiale del corso ritornato è sottoforma di jsonArray");
                            JSONArray dataJsonArray = (JSONArray) dataObject;
                            if (dataJsonArray != null)
                            {
                                for (int i=0; i<dataJsonArray.length(); i++)
                                {
                                    taskobject.add(dataJsonArray.getJSONObject(i));
                                }
                            }

                        }

                    }

                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return taskobject;
            }

            public boolean isOnline() {
                ConnectivityManager cm =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }


            @Override
            protected void onPostExecute(ArrayList<JSONObject> coursefiles) {
                super.onPostExecute(coursefiles);

                for (int i=0; i<coursefiles.size(); i++)
                {
                    String currentSectionId = coursefiles.get(i).optString("section");

                    sectionHasFile.put(currentSectionId, true);
                }
                System.out.println("MAP SECTION HAS FILE DOPO I TRUE " + sectionHasFile.toString());
                String idcorso = ((VCourse)getActivity()).idCorso;
                System.out.println("STO PER PRENDERE LE SEZIONI NEL CONTROLLER CON QUESTO ID " + idcorso.toString());
                System.out.println("NEL CONTROLLER C'E QUESTO SOTTO A SEZIONI " + Controller.getInstance().getCourseSections(idcorso).toString());
                listview.setAdapter(new MyAdapter(getActivity(), Controller.getInstance().getCourseSections(idcorso)));
            }

            private class MyAdapter extends BaseAdapter {

                private LayoutInflater inflater;
                private Context mContext;
                private ArrayList<JSONObject> data;

                public MyAdapter( Context context, ArrayList<JSONObject> data) {
                    this.data = data;
                    this.mContext = context;
                    this.inflater = LayoutInflater.from(mContext);
                }



                @Override
                public int getCount() {
                    return data.size();
                }

                @Override
                public Object getItem(int i) {
                    return data.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder;
                    if (convertView == null)
                    {
                        convertView = inflater.inflate(R.layout.adapter_section,null);
                        holder = new ViewHolder();

                        holder.title= (TextView) convertView.findViewById(R.id.adapter_section_sectiontitle);
                        holder.description= (TextView) convertView.findViewById(R.id.adapter_section_sectiondescription);
                        holder.clipicon=(ImageView) convertView.findViewById(R.id.adapter_section_imageView_hasfiles);
                        convertView.setTag(holder);
                    }
                    else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    JSONObject currentobj = (JSONObject)getItem(position);
                    if (!currentobj.optString("name").equals("null"))
                    {
                        holder.title.setText(currentobj.optString("name"));
                    }
                    else
                    {
                        holder.title.setText("Section " + currentobj.optString("section"));
                    }
                    holder.description.setText(currentobj.optString("summary"));
                    String currentsectionId= currentobj.optString("id");

                    System.out.println("MAP HAS FILE DENTRO ALL' ADAPTER " + sectionHasFile.toString());
                    if (sectionHasFile.get(currentsectionId))
                    {
                        System.out.println(currentsectionId.toString() + " HA DEI FILE -> SETVISIBILITY");
                        holder.clipicon.setVisibility(View.VISIBLE);
                    }
                    else {
                        System.out.println(currentsectionId.toString() + " NON HA DEI FILE -> SETVIS INVISIBLE");
                        holder.clipicon.setVisibility(View.INVISIBLE);
                    }
                    return convertView;
                }

                public class ViewHolder {
                    TextView title;
                    TextView description;
                    ImageView clipicon;
                }
            }



        }

    }
}
