package it.univaq.khestodocente.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.model.Chat;
import it.univaq.khestodocente.model.Course;

public class VRoomsCourse extends AppCompatActivity {

    private long idCorso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idCorso = Controller.currentCourseId;
        setContentView(R.layout.activity_vrooms_course);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<Course> corsi = Controller.getInstance().getUser().getCourses();
        String fullnamecorso= "";
        boolean trovato = false;
        for (int i=0; i<corsi.size() && !trovato; i++)
        {
            if (corsi.get(i).getId() == idCorso)
            {
                trovato = true;
                fullnamecorso = corsi.get(i).getName();
            }
        }
        if (!fullnamecorso.equals(""))
        {
            getSupportActionBar().setTitle( getResources().getString(R.string.rooms_of) + " " + fullnamecorso);
        }
    }





    public static class VChatsCourseFragment extends Fragment {

        private ListView listRooms;

        public VChatsCourseFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_rooms_course, container, false);
            listRooms= (ListView) root.findViewById(R.id.roomscourse_listview_listrooms);
            listRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    long idchat = ((Chat) listRooms.getAdapter().getItem(i)).getId();
                    Intent intent = new Intent(getActivity(),ChatDetails.class);
                    Controller.currentChatId=idchat;
                    getActivity().startActivity(intent);

                }
            });
            long idCorso = ((VRoomsCourse)getActivity()).idCorso;
            ArrayList<Chat> courseChats = Controller.getInstance().getUser().getCourse(idCorso).getChats();
            listRooms.setAdapter(new MyAdapter(getActivity(),courseChats));

            return  root;
        }

        private class MyAdapter extends BaseAdapter {

            private LayoutInflater inflater;
            private Context mContext;
            private ArrayList<Chat> data;

            public MyAdapter( Context context, ArrayList<Chat> chats) {
                this.data = chats;
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
                    convertView = inflater.inflate(R.layout.adapter_room,null);
                    holder = new ViewHolder();

                    holder.name= (TextView) convertView.findViewById(R.id.adapter_room_title);
                    holder.description= (TextView) convertView.findViewById(R.id.adapter_room_description);
                    convertView.setTag(holder);
                }
                else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Chat chat = (Chat)getItem(position);
                if (!chat.getTitle().equals("null"))
                {
                    holder.name.setText(chat.getTitle());
                }
                else
                {
                    holder.name.setText(getResources().getString(R.string.unnamed_chat));
                }
                if (((Chat) getItem(position)).getDescription().length() > 0){
                    holder.description.setText(chat.getDescription());
                    holder.description.setVisibility(View.VISIBLE);
                }
                else {
                    holder.description.setVisibility(View.GONE);
                }


                return convertView;
            }

            public class ViewHolder {
                TextView name;
                TextView description;
            }
        }
    }
}
