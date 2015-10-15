package it.univaq.khestodocente.view.fragment;

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
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.model.Course;
import it.univaq.khestodocente.view.activity.VRoomsCourse;

public class ChatFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "2";

        private ListView listCourses;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChatFragment newInstance(int sectionNumber) {
            ChatFragment fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ChatFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_professor_chat, container, false);

            listCourses = (ListView) rootView.findViewById(R.id.professor_chat_listView_listchat);
            listCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    long id = ((Course)listCourses.getAdapter().getItem(i)).getId();
                    Intent intent = new Intent(getActivity(), VRoomsCourse.class);
                    System.out.println("iesimo elemento nella list view " + listCourses.getAdapter().getItem(i).toString());
                    Controller.currentCourseId=id;
                    getActivity().startActivity(intent); //LANCIO LA NUOVA VIEW

                }
            });
            ArrayList<Course> courses = Controller.getInstance().getUser().getCourses();
            MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi,courses);
            listCourses.setAdapter(adapter);
            return rootView;
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if(((ProfessorHome) context).getArraylistjsonobjcourse() != null) {
            System.out.println("Dentro a chat -> passo nell' if");
            MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi,
                    ((ProfessorHome) context).getArraylistjsonobjcourse());
            listView.setAdapter(adapter);
        } else {
            System.out.println("Dentro a chat -> passo nell' else");
            ((ProfessorHome) context).setCallback(new ProfessorHome.OnRequestCallback() {
                @Override
                public void onComplete(ArrayList<JSONObject> data) {
                    MyAdapter adapter = new MyAdapter(getActivity(), R.layout.adapter_corsi, data);
                    listView.setAdapter(adapter);
                }
            }, 3);
        }
        */
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(getResources().getString(R.string.rooms_of) + " " + getItem(position).getName());
            return convertView;
        }

        public class ViewHolder{
            TextView name;
        }
    }
    }