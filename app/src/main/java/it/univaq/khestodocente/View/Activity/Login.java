package it.univaq.khestodocente.View.Activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.Controller.Controller;
import it.univaq.khestodocente.Model.Url;
import it.univaq.khestodocente.Utils.HelperJSON;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.login_title);
    }

    public static class LoginFragment extends Fragment {

        private EditText Username;
        private EditText Password;
        private Button Login_button;
        private TextView ErrorLabel;
        private TextView NoConnectionLabel;

        public LoginFragment() {}


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_login, container, false);

            Username = (EditText) root.findViewById(R.id.login_edittext_username);
            Username.setText("admin");
            Password = (EditText) root.findViewById(R.id.login_edittext_password);
            Password.setText("admin");
            Login_button = (Button) root.findViewById(R.id.login_button_login);
            Login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Login();
                }
            });

            ErrorLabel = (TextView) root.findViewById(R.id.login_textview_errorlabel);
            NoConnectionLabel= (TextView) root.findViewById(R.id.login_textview_noconnectionlabel);

            return root;
        }

        void Login () {
            String username = Username.getText().toString();
            String password = Password.getText().toString();

            System.out.println(username + " - " + password);

            new LoginTask().execute(username, password);


        }



        private class LoginTask extends AsyncTask<String, Void, Object>{

            @Override
            protected Object doInBackground(String... params) {
                Object taskobject = new Object();
                try {
                    URL url = Url.getLoginURL(params[0],params[1]);
                    System.out.println(url);

                    if (isOnline())
                    {
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setRequestMethod("GET");

                        int code = urlc.getResponseCode();
                        System.out.println("RESPONSE CODE " + code);

                        InputStream is;
                        if(code == HttpURLConnection.HTTP_OK) {
                            is = urlc.getInputStream();
                        } else is = urlc.getErrorStream();

                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line = br.readLine()) != null){
                            sb.append(line);
                        }
                        br.close();
                        String risultato = sb.toString();
                        JSONObject rootObj = new JSONObject(risultato);

                        if(rootObj.has("result")) {
                            Object dataObject = rootObj.get("result");
                            if (dataObject instanceof JSONObject) {
                                Controller.getInstance().setUser(HelperJSON.parseUserMoodle((JSONObject) dataObject));
                                taskobject = true;
                            } else if (dataObject instanceof Boolean) {
                                boolean b = (boolean) dataObject;
                                if (!b) {
                                    System.out.println("Authentication failed! Username or password is wrong!");
                                    taskobject = false;
                                }
                            }
                        } else {
                            taskobject = false;
                        }
                    }
                    else
                    {
                        System.out.println("Connessione assente");
                        taskobject= "no_connection";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
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
            protected void onPostExecute(Object s) {

                if (s instanceof Boolean)
                {
                    Boolean s_boolean = (Boolean) s;
                    if (!s_boolean)
                    {
                        ErrorLabel.setVisibility(View.VISIBLE);
                    }
                    else {
                        Intent intent = new Intent(getActivity(),ProfessorHome.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                }

                if (s instanceof String)
                {
                    String s_string = (String) s;
                    System.out.println(s_string);
                    if (s_string.equals("no_connection"))
                    {
                        NoConnectionLabel.setVisibility(View.VISIBLE);
                    }
                }
            }
        }


    }
}
