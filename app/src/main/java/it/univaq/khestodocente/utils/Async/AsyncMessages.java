package it.univaq.khestodocente.utils.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import it.univaq.khestodocente.main.Model.Message;
import it.univaq.khestodocente.utils.HelperJSON;

/**
 * Created by beniamino on 07/10/15.
 */
public class AsyncMessages extends AsyncTask<Integer, Void, List<Message>> {
    private Context _context;
    private boolean _isMoodle;

    public AsyncMessages(Context context) {
        _context = context;
    }

    @Override
    protected List<Message> doInBackground(Integer... params) {

        return HelperJSON.parseMessages(HelperRequest.getInstance(_context).getChatMoodleMessages(RunObject.cRoom.getId(), params[0]));

    }
}
