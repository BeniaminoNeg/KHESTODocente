package it.univaq.khestodocente.view.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.BufferedReader;
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
import it.univaq.khestodocente.model.Message;
import it.univaq.khestodocente.model.Url;
import it.univaq.khestodocente.view.adapter.MessageAdapter;
import it.univaq.khestodocente.utils.HelperJSON;

/**
 * Created by beniamino on 07/10/15.
 */
public class ChatDetails extends AppCompatActivity {

    private MessageAdapter _adapter;
    private ListView _listMessage;
    private EditText _editMessage;
    private ImageButton _buttonSend;

    private Handler _handler = new Handler();
    private Handler _handlerUpdate = new Handler();
    private boolean isStopped = false;

    private long idChat;
    private long idCorso;

    private List<Message> _messages = new ArrayList<Message>();

    private int _page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetails);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.title_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        idCorso = Controller.currentCourseId;
        idChat = Controller.currentChatId;



        initGraphic();

        updateMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStopped = false;
        if(_handlerUpdate == null) _handlerUpdate = new Handler();
        _handlerUpdate.post(new Runnable() {
            @Override
            public void run() {

                if (!isStopped) {
                    updateMessage();
                    if(_handlerUpdate!= null)_handlerUpdate.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        isStopped = true;
        if(_handlerUpdate!= null) _handlerUpdate.removeCallbacks(null);
        _handlerUpdate = null;
    }

    // TODO: FUNCTION TO GET OLDER MESSAGE

    private void initGraphic(){

        _listMessage = (ListView) findViewById(R.id.chatdetails_list_messages);
        _editMessage = (EditText) findViewById(R.id.chatdetails_edit_message);
        _buttonSend = (ImageButton) findViewById(R.id.chatdetails_button_send);

        _listMessage.setAdapter(_adapter = new MessageAdapter(this, _messages));

        _editMessage.addTextChangedListener(onChangetext);
        _buttonSend.setEnabled(false);
        _buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(_editMessage.getText().toString().length() > 0) {
                    Message message = new Message();
                    message.setIdUser(Controller.getInstance().getUser().getIdmoodle());
                    message.setName(Controller.getInstance().getUser().getFirstname());
                    message.setSurname(Controller.getInstance().getUser().getLastname());
                    message.setMessage(_editMessage.getText().toString());
                    message.setTimestamp(System.currentTimeMillis());

                    _editMessage.setText("");

                    new AsyncSend().execute(message.getMessage());

                    _messages.add(message);
                    _adapter.notifyDataSetChanged();

                }
            }
        });
    }

    private TextWatcher onChangetext = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(count > 0) _buttonSend.setEnabled(true);
            else _buttonSend.setEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void updateMessage(){

        new AsyncLoader().execute(_page);
    }

    private class AsyncLoader extends AsyncTask<Integer, Void, List<Message>> {

        @Override
        protected List<Message> doInBackground(Integer... params) {
            List<Message> tasklist = null;
            URL url = Url.getMoodleMessageURL(idChat, params[0]);
            System.out.println(url);
            if (isOnline()){
                    try {
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
                        tasklist = HelperJSON.parseMessages(risultato);
                    }catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return tasklist;
        }


        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            _messages = messages;
            _listMessage.setAdapter(_adapter = new MessageAdapter(ChatDetails.this, _messages));
        }
    }

    private class AsyncSend extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            long idUtente = Controller.getInstance().getUser().getIdmoodle();
            URL url = Url.getPutMessageURL(idChat, idUtente, params[0], idCorso);
            System.out.println(url);
            if (isOnline()){
                try {
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
                    System.out.println("SERVER REPLIED:");
                    System.out.println(risultato);
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new AsyncLoader().execute(_page);
        }
    }
}
