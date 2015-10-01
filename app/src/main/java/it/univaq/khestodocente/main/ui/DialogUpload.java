package it.univaq.khestodocente.main.ui;

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
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Url;
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

    private String courseId;

    private String sectionId;

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
        descriptionfile_edittext = (EditText) rootView.findViewById(R.id.upload_edittext_description);
        filePath = (TextView) rootView.findViewById(R.id.upload_textview_path);
        sfogliaButton = (Button) rootView.findViewById(R.id.upload_button_sfoglia);

        namefile = namefile_edittext.getText().toString();
        descriptionfile = descriptionfile_edittext.getText().toString();



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
                    File file = new File(urifile.toString());
                    new UploadFileTask().execute(file);
                }
                else
                {
                    System.out.println("Cazzo era null");
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
            URL url = new Url().getUploadfileURL();
            System.out.println("URL per l' upload: " + url);

            if (isOnline())
            {
                try {



                    Long longuserId = Controller.getInstance().getUser().getId();
                    String userId = longuserId.toString();
                    String username = Controller.getInstance().getUser().getUsername();
                    //System.out.println("La variabile map è: " + (Controller.getInstance().getmListcorses().toString()));
                    MultipartUtility multipart = new MultipartUtility(Url.URL_UPLOAD, "UTF-8");

                    multipart.addHeaderField("User-Agent", "CodeJava");
                    multipart.addHeaderField("Test-Header", "Header-Value");

                    multipart.addFormField("userid", userId);
                    multipart.addFormField("username", username);
                    multipart.addFormField("course", courseId);
                    multipart.addFormField("section", sectionId);
                    multipart.addFormField("postName", namefile);
                    multipart.addFormField("postText", descriptionfile);

                    System.out.println("files[0] " + files[0]);

                    multipart.addFilePart("fileUpload", files[0]);

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
            super.onPostExecute(list);
            //Context context = getApplicationContext();
            /*String text = "SERVER REPLIED: ";
            int duration = Toast.LENGTH_SHORT;
            for (String line : list)
            {
                text = text + line;
            }
            Toast toast;
            toast = Toast.makeText(getDialog().getContext(), text, duration);
            toast.show();
            */
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
