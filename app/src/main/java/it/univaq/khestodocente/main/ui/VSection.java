package it.univaq.khestodocente.main.ui;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

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
import java.util.Calendar;
import java.util.Locale;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Downloader;
import it.univaq.khestodocente.main.Model.Url;

public class VSection extends Activity {

    private String idsezione;

    private String idcorso;

    public String getIdsezione() {
        return idsezione;
    }

    public String getIdcorso() {
        return idcorso;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        idsezione = b.getString("idsection");
        idcorso = b.getString("idcourse");
        setContentView(R.layout.activity_section);

        ArrayList <JSONObject> sezioni= Controller.getInstance().getCourseSections(idcorso);
        System.out.println("sezioni= " + sezioni.toString());

        String namesection= "";
        String numbersection= "";
        boolean trovato = false;
        for (int i=0; i<sezioni.size() && !trovato; i++)
        {
            if (sezioni.get(i).optString("id").equals(idsezione))
            {
                trovato = true;
                namesection = sezioni.get(i).optString("name");
                numbersection= sezioni.get(i).optString("section");
            }
        }
        if (namesection.equals("null"))
        {
            setTitle("Section " + numbersection);
        }
        else
        {
            setTitle(namesection);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
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


    public static class CourseFragment extends Fragment {

        public CourseFragment() {
        }

        private ArrayList<JSONObject> sectionfiles;

        private ListView listview;

        private FloatingActionButton fab;

        public ArrayList<JSONObject> getSectionfiles() {
            return sectionfiles;
        }

        public void setSectionfiles(ArrayList<JSONObject> sectionfiles) {
            this.sectionfiles = sectionfiles;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_section, container, false);

            listview = (ListView) root.findViewById(R.id.section_listView_sectioncontent);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // Inserire qui cosa fare quando si clicca su un elemento del corso
                    JSONObject jsonobjitem = (JSONObject) listview.getAdapter().getItem(i);
                    new Downloader(getActivity()).download(jsonobjitem);
                }
            });

            fab = (FloatingActionButton) root.findViewById(R.id.course_fab_addfab);
            fab.attachToListView(listview);
            fab.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.DialogFragment dialog = new DialogUpload();
                    dialog.show(getFragmentManager(), "Uploader");
                }

            });

            String idcorso = ((VSection)getActivity()).idcorso;
            new CourseElementsTask().execute(idcorso);


            return root;
        }

        void AddContent(){
            //cosa fare per aggiungere un contenuto
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

                String idsezione = ((VSection)getActivity()).idsezione;
                sectionfiles=new ArrayList<JSONObject>();

                for (int i=0; i<coursefiles.size(); i++)
                {
                    if (coursefiles.get(i).optString("section").equals(idsezione))
                    {
                        sectionfiles.add(coursefiles.get(i));
                    }
                }

                System.out.println("sectionfiles: " + sectionfiles.toString());

                MyAdapter adapter = new MyAdapter(getActivity(),sectionfiles);
                listview.setAdapter(adapter);


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
                    if (convertView == null){
                        convertView= inflater.inflate(R.layout.adapter_file,null);
                        holder = new ViewHolder();
                        holder.filename=(TextView) convertView.findViewById(R.id.adapter_file_textname);
                        holder.author=(TextView) convertView.findViewById(R.id.adapter_file_author);
                        holder.date=(TextView) convertView.findViewById(R.id.adapter_file_date);
                        convertView.setTag(holder);
                    }
                    else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    JSONObject currentobj = (JSONObject)getItem(position);
                    holder.filename.setText(currentobj.optString("filename"));
                    holder.author.setText("Author: " + currentobj.optString("author"));
                    String stringTimestamp = currentobj.optString("timemodified");
                    String date = Controller.getInstance().parseMyDate(stringTimestamp);
                    System.out.println("DATA " + date);

                    holder.date.setText(date);

                    return convertView;
                }

                public class ViewHolder {
                    TextView filename;
                    TextView author;
                    TextView date;
                }
            }

        }
    }


}
