package com.technofreak.bdtextbooks;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private Activity context;
    private List<Comment> commentList;

    public CommentListAdapter(Activity context, List<Comment>commentList) {
        super(context,R.layout.list_row,commentList);
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewRow = inflater.inflate(R.layout.list_row,null,true);

        //alternating row color
        if (position%2 == 0){
            listViewRow.setBackgroundColor(Color.parseColor("#C8FFF3E0"));
        }else{
            listViewRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        TextView textViewUserName = listViewRow.findViewById(R.id.list_userName);
        TextView textViewDate = listViewRow.findViewById(R.id.list_date);
        TextView textViewText = listViewRow.findViewById(R.id.list_text);

        Comment comment = commentList.get(position);
        textViewUserName.setText(comment.getUserName());
        textViewDate.setText(comment.getDate());
        textViewText.setText(comment.getText());

        return listViewRow;
    }

}
