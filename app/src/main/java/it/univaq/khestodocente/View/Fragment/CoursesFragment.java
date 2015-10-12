package it.univaq.khestodocente.View.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.Controller.Controller;
import it.univaq.khestodocente.Model.Course;
import it.univaq.khestodocente.View.Activity.VCourse;

public class CoursesFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "0";

    private ListView listView;

    public static CoursesFragment newInstance(int sectionNumber) {
        CoursesFragment fragment = new CoursesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CoursesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_professor_course, container, false);

        listView = (ListView) rootView.findViewById(R.id.professor_course_listView_listcourses);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long id = ((Course)listView.getAdapter().getItem(i)).getId();
                Intent intent = new Intent(getActivity(), VCourse.class);
                System.out.println("iesimo elemento nella list view " + listView.getAdapter().getItem(i).toString());
                intent.putExtra("Id",id);
                getActivity().startActivity(intent); //LANCIO LA NUOVA VIEW

            }
        });
        ArrayList<Course> courses = Controller.getInstance().getUser().getCourses();
        MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi,courses);
        listView.setAdapter(adapter);
        return rootView;
    }

    private class MyAdapter extends ArrayAdapter<Course> {

        public MyAdapter(Context context, int resource, ArrayList<Course> courses) {
            super(context, resource, courses);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_corsi, null);
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.adapter_corsi_text_name);
                holder.desc = (TextView) convertView.findViewById(R.id.adapter_corsi_text_desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(getItem(position).getName());
            if(getItem(position).getDescription().length() > 0) {
                holder.desc.setText(getItem(position).getDescription());
                holder.desc.setVisibility(View.VISIBLE);
            } else holder.desc.setVisibility(View.GONE);
            return convertView;
        }

        public class ViewHolder{
            TextView name, desc;
        }
    }
}