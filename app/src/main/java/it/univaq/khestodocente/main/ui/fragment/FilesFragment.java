package it.univaq.khestodocente.main.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Downloader;
import it.univaq.khestodocente.main.Model.Url;
import it.univaq.khestodocente.main.ui.ProfessorHome;

public class FilesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "1";

        private ArrayList<String> arrayidcourse;
        private HashMap <String,ArrayList<JSONObject>> mapmateriale;


    private ListView listView;

    private FloatingActionButton fab;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FilesFragment newInstance(int sectionNumber) {
            FilesFragment fragment = new FilesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FilesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_professor_files, container, false);

            listView = (ListView) rootView.findViewById(R.id.professor_content_listView_listcontent);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap map = (HashMap) listView.getAdapter().getItem(i);
                    JSONObject jsonobjitem = (JSONObject) map.get("material");
                    new Downloader(getActivity()).download(jsonobjitem);
                }
            });
            return rootView;
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(Controller.getInstance().getArrlistJSONobjCourses() != null) {
            new ContentTask().execute(Controller.getInstance().getArrlistJSONobjCourses());
        } else {
            ((ProfessorHome) context).setCallback(new ProfessorHome.OnRequestCallback() {
                @Override
                public void onComplete(ArrayList<JSONObject> data) {
                    new ContentTask().execute(data);
                }
            }, 2);
        }
    }



    private class ContentTask extends AsyncTask <ArrayList<JSONObject>, Void, List<Map<String, Object>>>{
        @Override
        protected List<Map<String, Object>> doInBackground(ArrayList<JSONObject>... params) {

            List<Map<String, Object>> listMap = new ArrayList<>();
            if(params[0] != null){
                for(int i = 0; i < params[0].size(); ++i){
                    JSONObject json = params[0].get(i);
                    String result = doRequest(json.optString("id"));
                    System.out.println(result);
                    if(result != null) {
                        JSONArray array = parseJson(result);
                        if(array != null){
                            for(int j = 0; j < array.length(); j++) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("course", json);
                                map.put("material", array.optJSONObject(j));
                                listMap.add(map);
                                Controller.getInstance().setmListcorses(listMap);
                            }
                        }
                    }
                }
            }
            return listMap;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> data) {
            listView.setAdapter(new MyAdapter(getActivity(), data));
        }

        private JSONArray parseJson(String result){

            try {
                JSONObject jsonObj = new JSONObject(result);
                return jsonObj.optJSONArray("result");
            } catch (JSONException e) {}
            return null;
        }

        private String doRequest(String id){

            try {
                URL url = new Url().getFilescourseURL(id);
                System.out.println("URL nel task per i contenuti in content fragment " + url);

                if(Controller.getInstance().isOnline(getContext()))
                {
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestMethod("GET");

                    int code = urlc.getResponseCode();
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
                    return sb.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context mContext;
        private List<Map<String, Object>> data;

        public MyAdapter(Context context, List<Map<String, Object>> objs) {
            mContext = context;
            data = objs;
            inflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.adapter_file, null);
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.adapter_file_textname);
                holder.course = (TextView) convertView.findViewById(R.id.adapter_file_course);
                holder.author = (TextView) convertView.findViewById(R.id.adapter_file_author);
                holder.date = (TextView) convertView.findViewById(R.id.adapter_file_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> map = getItem(position);
            JSONObject course = (JSONObject) map.get("course");
            JSONObject material = (JSONObject) map.get("material");
            holder.course.setText(course.optString("fullname"));
            holder.name.setText(material.optString("filename"));
            holder.author.setText("Author: " + material.optString("author"));
            String stringTimestamp = material.optString("timemodified");
            String date = Controller.getInstance().parseMyDate(stringTimestamp);
            holder.date.setText(date);

            return convertView;
        }

        public class ViewHolder{
            TextView name;
            TextView course;
            TextView author;
            TextView date;
        }
    }

    }