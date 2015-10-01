package it.univaq.khestodocente.main.ui.fragment;

import android.content.Context;
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

public class ChatFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "2";

        private ListView listView;

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

            listView = (ListView) rootView.findViewById(R.id.professor_chat_listView_listchat);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String id = ((JSONObject) listView.getAdapter().getItem(i)).optString("id");
                    String fullname = ((JSONObject) listView.getAdapter().getItem(i)).optString("fullname");

                    System.out.println("Qui ci va la chat");

                    /*
                    Intent intent = new Intent(getActivity(), nomeview  );
                    intent.putExtra("Id",id);
                    intent.putExtra("fullname",fullname);

                    getActivity().startActivity(intent); //LANCIO LA NUOVA VIEW

                    */

                }
            });
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