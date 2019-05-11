package com.technofreak.bdtextbooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForumActivity extends AppCompatActivity {

    private EditText etComment;
    DatabaseReference myref,myrefAccepted;
    private ListView listView;
    private String userName;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        String bookname = getIntent().getStringExtra("BOOKNAME");

        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+" ► "+bookname;

        myref = FirebaseDatabase.getInstance().getReference("Books/"+bookname).child("requested");
        myrefAccepted = FirebaseDatabase.getInstance().getReference("Books/"+bookname).child("accepted");
        etComment = findViewById(R.id.comment);
        listView = findViewById(R.id.commentListView);
        commentList = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Comment comment = commentList.get(position);
                Intent intent = new Intent(ForumActivity.this,ReplyActivity.class);
                intent.putExtra("COMMENTID",comment.getCommentID());
                intent.putExtra("BOOKNAME",comment.getBookName());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        myrefAccepted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();

                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    Comment comment =  commentSnapshot.getValue(Comment.class);
                    if (commentSnapshot.hasChild("Reply")){
                        long n = commentSnapshot.child("Reply").getChildrenCount();
                        comment.date += "\nReplies: "+n;
                    }

                    commentList.add(comment);
                }

                CommentListAdapter adapter = new CommentListAdapter(ForumActivity.this,commentList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void postComment(View view) {
        String cmnt = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(cmnt)){
            Toast.makeText(this,"Comment field cannot be left blank",Toast.LENGTH_SHORT).show();
        }
        else {
            String cmntID = myref.push().getKey();
            //get current Timestamp
            String date =  new SimpleDateFormat("HH:mm ▪ dd/MM/yy", Locale.getDefault()).format(new Date());
            Comment comment = new Comment(cmntID,userName,cmnt,date);

            myref.child(cmntID).setValue(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Comment sent for approval",Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                }
            });
        }

    }

}
