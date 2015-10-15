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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.model.Course;
import it.univaq.khestodocente.model.Section;

public class VCourse extends AppCompatActivity {

    private long idCorso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idCorso = Controller.getInstance().currentCourseId;
        setContentView(R.layout.activity_course);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.vcourse_title);
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
            getSupportActionBar().setTitle(fullnamecorso);
        }

    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class VCourseFragment extends Fragment {

        private ListView listview;

        private HashMap<Long,Boolean> sectionHasFile;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_course, container, false);
            listview= (ListView) root.findViewById(R.id.course_listView_listsections);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    long idsection = ((Section) listview.getAdapter().getItem(i)).getId();
                    String sectionnumber = ((Section) listview.getAdapter().getItem(i)).getNumber();

                    System.out.println("HAI CLICCATO SULLA SECTION CON ID " + idsection + " E SECTION NUMBER " + sectionnumber);
                    Intent intent = new Intent(getActivity(),VSection.class);
                    Controller.currentSectionId=idsection;
                    getActivity().startActivity(intent);
                }
            });
            long idCorso = ((VCourse)getActivity()).idCorso;
            sectionHasFile = new HashMap<>();
            ArrayList<Section> sectionsCourse = Controller.getInstance().getUser().getCourse(idCorso).getSections();
            for (int i=0; i<sectionsCourse.size(); i++)
            {
                Long currentSectionId = sectionsCourse.get(i).getId();
                sectionHasFile.put(currentSectionId,false);
            }
            System.out.println("MAP HAS FILE APPENA CREATA " + sectionHasFile.toString());

            ArrayList<it.univaq.khestodocente.model.File> filesCourse = Controller.getInstance().getUser().getCourse(idCorso).getFiles();
            for (int i=0; i < filesCourse.size(); i++)
            {
                long currentSectionId = filesCourse.get(i).getSectionid();

                sectionHasFile.put(currentSectionId, true);
            }

            System.out.println("MAP SECTION HAS FILE DOPO I TRUE " + sectionHasFile.toString());

            listview.setAdapter(new MyAdapter(getActivity(), sectionsCourse));

            return root;
        }

        private class MyAdapter extends BaseAdapter {

            private LayoutInflater inflater;
            private Context mContext;
            private ArrayList<Section> data;

            public MyAdapter( Context context, ArrayList<Section> sections) {
                this.data = sections;
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

                Section section = (Section)getItem(position);
                if (!section.getTitle().equals("null"))
                {
                    holder.title.setText(section.getTitle());
                }
                else
                {
                    holder.title.setText("Section " + section.getNumber());
                }

                if (((Section) getItem(position)).getDescription().length() > 0){
                    holder.description.setText(section.getDescription());
                    holder.description.setVisibility(View.VISIBLE);
                }
                else {
                    holder.description.setVisibility(View.GONE);
                }

                long currentsectionId= section.getId();


                if (sectionHasFile.get(currentsectionId))
                {
                    holder.clipicon.setVisibility(View.VISIBLE);
                }
                else {
                    holder.clipicon.setVisibility(View.GONE);
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
