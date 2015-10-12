package it.univaq.khestodocente.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.Controller.Controller;
import it.univaq.khestodocente.Model.Course;
import it.univaq.khestodocente.Model.File;
import it.univaq.khestodocente.Utils.Downloader;

public class FilesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "1";

    private ListView listView;



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
                    File file = (File) map.get("material");
                    new Downloader(getActivity()).download(file);
                }
            });

            ArrayList<File> allFilesByTime = new ArrayList<>();
            for (int i=0; i<Controller.getInstance().getUser().getCourses().size(); i++)
            {
                allFilesByTime.addAll(Controller.getInstance().getUser().getCourses().get(i).getFiles());
            }

            Collections.sort(allFilesByTime, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.compareTo(f2);
                }
            });

            MyAdapter adapter = new MyAdapter(getActivity(),allFilesByTime);
            listView.setAdapter(adapter);
            return rootView;
        }

    private class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context mContext;
        private ArrayList<File> data;

        public MyAdapter(Context context, ArrayList<File> files) {
            mContext = context;
            data = files;
            inflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public File getItem(int i) {
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

            File currentFile = getItem(position);
            long currentfileId = currentFile.getId();
            String nameCourse = "";
            boolean trovato = false;
            ArrayList<Course> courses = Controller.getInstance().getUser().getCourses();
            for (int i=0; i<courses.size() && !trovato; i++){
                Course iesimocorso = courses.get(i);
                for (int j=0; j<iesimocorso.getFiles().size() && !trovato; j++){
                    File jesimofile = iesimocorso.getFiles().get(j);
                    if (jesimofile.getId() == currentfileId){
                        trovato = true;
                        nameCourse = iesimocorso.getName();
                    }
                }
            }

            holder.course.setText(nameCourse);
            holder.name.setText(currentFile.getFilename());
            holder.author.setText(currentFile.getAuthor());
            long timestamp = currentFile.getTime();
            String date = Controller.getInstance().parseMyDate(timestamp);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    }