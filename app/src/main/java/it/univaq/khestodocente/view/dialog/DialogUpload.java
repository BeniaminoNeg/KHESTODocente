package it.univaq.khestodocente.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.model.Url;
import it.univaq.khestodocente.view.activity.VSection;
import it.univaq.khestodocente.utils.HelperJSON;
import it.univaq.khestodocente.utils.MultipartUtility;


/**
 * Created by beniamino on 23/09/15.
 */
public class DialogUpload extends DialogFragment {

    private EditText namefile_edittext;

    private String namefile;

    private EditText descriptionfile_edittext;

    private String descriptionfile;

    private TextView filePath;

    private Button sfogliaButton;

    public static final int REQUESTFILE = 9001;

    private Uri urifile;

    private Long courseId;

    private Long sectionId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //builder.setView(inflater.inflate(R.layout.dialog_upload, null));

        View rootView = inflater.inflate(R.layout.dialog_upload,null);

        Activity parent = getActivity();
        if (parent instanceof VSection)
        {
            courseId = ((VSection)getActivity()).getIdcorso();
            sectionId = ((VSection)getActivity()).getIdsezione();
            System.out.println("SALVERO IL FILE IN QUESTA SECTION " + sectionId);
        }

        namefile_edittext = (EditText) rootView.findViewById(R.id.upload_edittext_namefile);
        namefile_edittext.setText("Nome File di prova");
        descriptionfile_edittext = (EditText) rootView.findViewById(R.id.upload_edittext_description);
        descriptionfile_edittext.setText("Description di mannaggia santa");
        filePath = (TextView) rootView.findViewById(R.id.upload_textview_path);
        sfogliaButton = (Button) rootView.findViewById(R.id.upload_button_sfoglia);





        sfogliaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // This always works
                Intent i = new Intent(getActivity(), FilePickerActivity.class);
                // This works if you defined the intent filter
                //Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, REQUESTFILE);
            }
        });

        builder.setView(rootView);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //FARE LA RICHIESTA HTTP

                if (urifile != null)
                {
                    System.out.println("Non era null");
                    namefile = namefile_edittext.getText().toString();
                    System.out.println("NAMEFILE " + namefile);
                    descriptionfile = descriptionfile_edittext.getText().toString();
                    System.out.println("DESCRIPTIONFILE " + descriptionfile);
                    File file = new File(urifile.toString());
                    new UploadFileTask().execute(file);
                }
                else
                {
                    System.out.println("era null");
                }

            }
        });
        builder.setNegativeButton("annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    private class UploadFileTask extends AsyncTask <File,Void,List<String>>{
        @Override
        protected List<String> doInBackground(File... files) {
            List<String> taskobject = new ArrayList<String>();
            URL url = Url.getUploadfileURL();
            System.out.println("URL per l' upload: " + url);

            if (isOnline())
            {
                try {
                    Long longuserId = Controller.getInstance().getUser().getIdmoodle();
                    String userId = longuserId.toString();
                    String username = Controller.getInstance().getUser().getUsername();
                    String stringCourseId = courseId.toString();
                    String stringSectionId = sectionId.toString();

                    URL u = Url.getUploadfileURL();
                    MultipartUtility multipart = new MultipartUtility(u, "UTF-8");

                    multipart.addHeaderField("User-Agent", "JavaCode");
                    multipart.addHeaderField("Test-Header", "Header-Value");
                    multipart.addFilePart("fileUpload", files[0]);

                    multipart.addFormField("userid", userId);
                    multipart.addFormField("username", username);
                    multipart.addFormField("course", stringCourseId);
                    multipart.addFormField("section", stringSectionId);
                    multipart.addFormField("postName", namefile);
                    multipart.addFormField("postText", descriptionfile);

                    System.out.println("files[0] " + files[0]);

                    List<String> response = multipart.finish();

                    System.out.println("SERVER REPLIED:");

                    for (String line : response) {
                        System.out.println(line);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        protected void onPostExecute(List<String> list) {
            /*
            super.onPostExecute(list);
            String text = "SERVER REPLIED: ";
            int duration = Toast.LENGTH_SHORT;
            for (String line : list)
            {
                text = text + line;
            }
            Toast toast;
            toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
            */
            // TODO aggiornare il controller con il nuovo file

        }
    }

    private class UpdateFileListTask extends AsyncTask <Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String risultato = "";
            URL url = Url.getFilescourseURL(courseId.toString());
            if (isOnline()) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestMethod("GET");

                    int code = urlc.getResponseCode();
                    System.out.println("RESPONSE CODE " + code);

                    InputStream is;
                    if (code == HttpURLConnection.HTTP_OK) {
                        is = urlc.getInputStream();
                    } else is = urlc.getErrorStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    risultato = sb.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return risultato;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        @Override
        protected void onPostExecute(String risultato) {
            ArrayList<it.univaq.khestodocente.model.File> filescourse = (ArrayList<it.univaq.khestodocente.model.File>) HelperJSON.parseFilesCourse(risultato);
            Controller.getInstance().getUser().getCourse(courseId).setFiles(filescourse);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUESTFILE && resultCode == Activity.RESULT_OK){
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            System.out.println(uri.toString());
                            urifile=uri;
                            filePath.setText(uri.toString());
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            System.out.println(uri.toString());
                            urifile=uri;
                            filePath.setText(uri.toString());
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                System.out.println(uri.toString());
                urifile=uri;
                filePath.setText(uri.toString());
            }
        }
    }
}
