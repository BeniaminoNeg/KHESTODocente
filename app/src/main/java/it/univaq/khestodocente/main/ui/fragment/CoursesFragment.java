package it.univaq.khestodocente.main.ui.fragment;

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

import org.json.JSONObject;

import java.util.ArrayList;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.ui.ProfessorHome;
import it.univaq.khestodocente.main.ui.VCourse;
import it.univaq.khestodocente.main.ui.VSection;

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
                    String id = ((JSONObject) listView.getAdapter().getItem(i)).optString("id");
                    Intent intent = new Intent(getActivity(), VCourse.class);
                    System.out.println("iesimo elemento nella list view " + listView.getAdapter().getItem(i).toString());
                    intent.putExtra("Id",id);
                    getActivity().startActivity(intent); //LANCIO LA NUOVA VIEW

                }
            });

            //System.out.println("Sone dentro al fragment, ARRAYLIST DELL' ACTIVITY: " + ((ProfessorHome) getActivity()).arraylistjsonobjcourse.toString());
            return rootView;
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(Controller.getInstance().getArrlistJSONobjCourses() != null) {
            MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi,
                    Controller.getInstance().getArrlistJSONobjCourses());
            System.out.println("PASSO DI QUA");
            listView.setAdapter(adapter);
        } else {
            ((ProfessorHome) context).setCallback(new ProfessorHome.OnRequestCallback() {
                @Override
                public void onComplete(ArrayList<JSONObject> data) {
                    MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi, data);
                    listView.setAdapter(adapter);
                }
            }, 1);
        }
    }

    private class MyAdapter extends ArrayAdapter<JSONObject> {

            public MyAdapter(Context context, int resource, ArrayList<JSONObject> objs) {
                super(context, resource, objs);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if(convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_corsi, null);
                    holder = new ViewHolder();

                    holder.name = (TextView) convertView.findViewById(R.id.adapter_corsi_text_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.name.setText(getItem(position).optString("fullname"));

                return convertView;
            }

            public class ViewHolder{
                TextView name;
            }
        }
    }