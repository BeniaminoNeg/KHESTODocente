package it.univaq.khestodocente.View.Activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.Controller.Controller;
import it.univaq.khestodocente.Model.File;
import it.univaq.khestodocente.Model.Section;
import it.univaq.khestodocente.View.Dialog.DialogUpload;
import it.univaq.khestodocente.Utils.Downloader;

public class VSection extends AppCompatActivity {

    private long idsezione;

    private long idcorso;

    public long getIdsezione() {
        return idsezione;
    }

    public long getIdcorso() {
        return idcorso;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        idsezione = b.getLong("idsection");
        idcorso = b.getLong("idcourse");
        setContentView(R.layout.activity_section);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Section section = Controller.getInstance().getUser().getCourse(idcorso).getSection(idsezione);

        if (section.getTitle().equals("null")) {
            getSupportActionBar().setTitle("Section " + section.getNumber());
        } else {
            getSupportActionBar().setTitle(section.getTitle());
        }
    }

    public static class CourseFragment extends Fragment {

        public CourseFragment() {
        }

        private ListView listview;

        private FloatingActionButton fab;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_section, container, false);

            listview = (ListView) root.findViewById(R.id.section_listView_sectioncontent);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // Inserire qui cosa fare quando si clicca su un elemento del corso
                    File file = (File) listview.getAdapter().getItem(i);
                    new Downloader(getActivity()).download(file);
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

            long idcorso = ((VSection)getActivity()).idcorso;
            long idsezione = ((VSection)getActivity()).idsezione;

            ArrayList<File> filesCourse = Controller.getInstance().getUser().getCourse(idcorso).getFiles();
            ArrayList<File> filesSection = new ArrayList<File>();

            for (int i=0; i<filesCourse.size(); i++)
            {
                //System.out.println("id del " + i +"esimo file: " + filesCourse.get(i).getId() + " ----> confronto con id della sezione: "+idsezione );
                if (filesCourse.get(i).getSectionid() == idsezione)
                {
                    filesSection.add(filesCourse.get(i));
                }
            }

            System.out.println("sectionfiles: " + filesSection.toString());

            MyAdapter adapter = new MyAdapter(getActivity(),filesSection);
            listview.setAdapter(adapter);

            return root;
        }
        private class MyAdapter extends BaseAdapter {

            private LayoutInflater inflater;
            private Context mContext;
            private ArrayList<File> data;

            public MyAdapter( Context context, ArrayList<File> data) {
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
                File currentfile = (File)getItem(position);
                holder.filename.setText(currentfile.getFilename());
                holder.author.setText("Author: " + currentfile.getAuthor());
                long timestamp = currentfile.getTime();
                String date = Controller.getInstance().parseMyDate(timestamp);
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
