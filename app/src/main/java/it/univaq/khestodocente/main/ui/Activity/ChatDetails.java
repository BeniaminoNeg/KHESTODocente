package it.univaq.khestodocente.main.ui.Activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Message;
import it.univaq.khestodocente.main.ui.Adapter.MessageAdapter;

/**
 * Created by beniamino on 07/10/15.
 */
public class ChatDetails extends ActionBarActivity {
    private Toolbar _toolbar;
    private Handler _handler = new Handler();

    private MessageAdapter _adapter;
    private ListView _listMessage;
    private EditText _editMessage;
    private ImageButton _buttonSend;

    private ArrayList<Message> _messages = new ArrayList<Message>();

    private int _page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetails);


        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
//        _toolbar.setBackgroundColor(getResources().getColor(R.color.chat_toolbar_background));
        _toolbar.setTitleTextColor(Color.WHITE);
        _toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        _handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle b = getIntent().getExtras();
                long idCorso = b.getLong("idcourse");
                long idChat = b.getLong("idchat");
                _toolbar.setTitle(Controller.getInstance().getUser().getCourse(idCorso).getChat(idChat).getTitle().toUpperCase(Locale.getDefault()));
            }
        });

        initGraphic();

        updateMessage();
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

                    // TODO: SEND TO SERVER
                    /**
                     * Change this line: Send to server and notify to update last messages
                     * from timestamp (last message timestamp)
                     */
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

        new AsyncLoader().execute(Controller.getInstance(this).doMessageRequest(_isMoodle, _page));
    }

    private class AsyncLoader extends AsyncTask<Message>, Void, Void> {

        @Override
        protected Void doInBackground(List<Message>... params) {

            if(params[0] != null) {
                for(Message m : params[0]){
                    _messages.add(m);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ((MessageAdapter) _listMessage.getAdapter()).notifyDataSetChanged();
            _listMessage.invalidateViews();
        }
    }
}
