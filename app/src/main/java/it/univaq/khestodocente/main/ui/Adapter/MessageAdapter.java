package it.univaq.khestodocente.main.ui.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import it.univaq.khestodocente.R;
import it.univaq.khestodocente.controller.Controller;
import it.univaq.khestodocente.main.Model.Message;

/**
 * Created by beniamino on 07/10/15.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private Context _context;

    public MessageAdapter(Context context, List<Message> objects) {

        super(context, R.layout.adapter_chat, objects);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView == null){

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_chat, null);
            holder = new ViewHolder();

            holder.user = (TextView) convertView.findViewById(R.id.chat_text_user);
            holder.time = (TextView) convertView.findViewById(R.id.chat_text_time);
            holder.message = (TextView) convertView.findViewById(R.id.chat_text_message);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.chat_layout_main);

            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        Message item = getItem(position);
        holder.user.setText(item.getName() + " " + item.getSurname());
        holder.time.setText(Controller.getInstance().parseMyDate(getItem(position).getTimestamp()));
        holder.message.setText(item.getMessage());

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.layout.getLayoutParams();
        if(item.getIdUser() == Controller.getInstance().getUser().getIdmoodle()) {
            holder.layout.setBackgroundResource(R.drawable.chat_bubble_right);
            lp.gravity = Gravity.RIGHT;
        }
        else {
            holder.layout.setBackgroundResource(R.drawable.chat_bubble_left);
            lp.gravity = Gravity.LEFT;
        }
        holder.layout.setLayoutParams(lp);

        return convertView;
    }

    static class ViewHolder{

        TextView user;
        TextView time;
        TextView message;

        LinearLayout layout;
    }
}
