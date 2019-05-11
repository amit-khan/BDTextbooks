package com.technofreak.bdtextbooks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private Spinner spinner;
    private String[] allbooks;
    private List<Comment> commentList;
    private CommentListAdapter adapter;
    private DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Pending Posts");

        listView = findViewById(R.id.req_lv);
        spinner = findViewById(R.id.spinner);
        allbooks = new String[]{"ban_1", "eng_1"}; //default for cls 1
        commentList = new ArrayList<>();
        adapter = new CommentListAdapter(AdminActivity.this,commentList);
        listView.setAdapter(adapter);

        myref = FirebaseDatabase.getInstance().getReference("Books");

        setSpinnerListener();
        setListviewListener();

    }


    private void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        allbooks = new String[]{"ban_1", "eng_1"};
                        break;
                    case 1:
                        allbooks = new String[]{"ban_2","mat_2","eng_2"};
                    break;
                    case 2:
                        allbooks = new String[]{"ban_3","mat_3","eng_3","isl_3","hin_3","sci_3"};
                        break;
                    case 3:
                        allbooks = new String[]{"ban_4","mat_4","eng_4","isl_4","hin_4","sci_4"};
                        break;
                    case 4:
                        allbooks = new String[]{"ban_5","mat_5","eng_5","isl_5","hin_5","sci_5"};
                        break;
                    case 5:
                        allbooks = new String[]{"ban1_6","mat_6","eng_6","isl_6","hin_6","sci_6","ban2_6","info_6"};
                        break;
                    case 6:
                        allbooks = new String[]{"ban1_7","mat_7","eng_7","isl_7","hin_7","kri_7","ban2_7","info_7"};
                        break;
                    case 7:
                        allbooks = new String[]{"ban1_8","mat_8","eng_8","isl_8","sci_8","ban2_8","info_8"};
                        break;
                    case 8:
                        allbooks = new String[]{"ban1_9","mat_9","chem_9","bio_9","sci_9","ban2_9","info_9"};
                        break;
                }
                populateListview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setListviewListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Comment comment = commentList.get(position);
                final String cmntID=comment.getCommentID();
                final String book = comment.getBookName();

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(AdminActivity.this);
                adBuilder.setMessage("Select action");

                adBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference temp = myref.child(book+"/accepted").child(cmntID);
                        temp.setValue(comment);
                        temp=myref.child(book+"/requested").child(cmntID);
                        temp.removeValue();

                        commentList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                adBuilder.setNegativeButton("Reject",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference temp = myref.child(book+"/requested").child(cmntID);
                        temp.removeValue();
                        commentList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"Rejected",Toast.LENGTH_SHORT).show();
                    }
                });

                adBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish
                    }
                });

                AlertDialog alertDialog = adBuilder.create();
                alertDialog.show();

            }
        });

    }


    private void populateListview() {
        commentList.clear();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    Comment comment =  commentSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        for (String bookname : allbooks){
            DatabaseReference temp = myref.child(bookname).child("requested");
            temp.addListenerForSingleValueEvent(eventListener);
        }

    }

}
