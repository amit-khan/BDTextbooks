package com.technofreak.bdtextbooks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class ReplyActivity extends AppCompatActivity {

    private EditText etComment;
    private Button replyBtn;
    DatabaseReference myref;
    private ListView listView;
    private String userName,bookname,commentID;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        setTitle("Replies");

        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        bookname = getIntent().getStringExtra("BOOKNAME");
        commentID = getIntent().getStringExtra("COMMENTID");
        myref = FirebaseDatabase.getInstance().getReference("Books/"+bookname+"/accepted").child(commentID).child("Reply");
        etComment = findViewById(R.id.comment);
        listView = findViewById(R.id.commentListView);
        replyBtn = findViewById(R.id.btn_post);
        commentList = new ArrayList<>();

        etComment.setHint("Reply to this post");
        replyBtn.setText("Reply");

    }

    @Override
    protected void onStart() {
        super.onStart();
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    Comment comment =  commentSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                CommentListAdapter adapter = new CommentListAdapter(ReplyActivity.this,commentList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void postComment(View view) {
        String reply = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(reply)){
            Toast.makeText(this,"Comment field cannot be left blank",Toast.LENGTH_SHORT).show();
        }
        else {
            String cmntID = myref.push().getKey();
            //get current Timestamp
            String date =  new SimpleDateFormat("HH:mm ▪ dd/MM/yy", Locale.getDefault()).format(new Date());
            Comment comment = new Comment(cmntID,userName,reply,date);

            myref.child(cmntID).setValue(comment);
            etComment.setText("");
        }

    }
}
