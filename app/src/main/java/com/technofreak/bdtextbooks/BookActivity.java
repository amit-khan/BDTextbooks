package com.technofreak.bdtextbooks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technofreak.bdtextbooks.Classes.Class1;
import com.technofreak.bdtextbooks.Classes.Class2;
import com.technofreak.bdtextbooks.Classes.Class3;
import com.technofreak.bdtextbooks.Classes.Class4;
import com.technofreak.bdtextbooks.Classes.Class5;
import com.technofreak.bdtextbooks.Classes.Class6;
import com.technofreak.bdtextbooks.Classes.Class7;
import com.technofreak.bdtextbooks.Classes.Class8;
import com.technofreak.bdtextbooks.Classes.Class9;

import java.io.File;

public class BookActivity extends AppCompatActivity {

    TextView textView;
    private Button requestBtn;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        textView = findViewById(R.id.userNameTv);
        requestBtn = findViewById(R.id.request_btn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    getUserInfo(user);
                } else{
                    Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                }
            }
        };

        final String[] it ={"Class 1","Class 2","Class 3","Class 4","Class 5","Class 6","Class 7","Class 8", "Class 9-10"};
        ListView list = findViewById(R.id.listview1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,it);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(BookActivity.this, Class1.class));
                        break;
                    case 1:
                        startActivity(new Intent(BookActivity.this, Class2.class));
                        break;
                    case 2:
                        startActivity(new Intent(BookActivity.this, Class3.class));
                        break;
                    case 3:
                        startActivity(new Intent(BookActivity.this, Class4.class));
                        break;
                    case 4:
                        startActivity(new Intent(BookActivity.this, Class5.class));
                        break;
                    case 5:
                        startActivity(new Intent(BookActivity.this, Class6.class));
                        break;
                    case 6:
                        startActivity(new Intent(BookActivity.this, Class7.class));
                        break;
                    case 7:
                        startActivity(new Intent(BookActivity.this, Class8.class));
                        break;
                    default:
                        startActivity(new Intent(BookActivity.this, Class9.class));
                }

            }
        });

        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        } else {
            File folder = new File(Environment.getExternalStorageDirectory().toString()+ "/Android/data/BDtextbook");
            if(!folder.exists()){
                folder.mkdir();
            }
        }


    } //onCreate


    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    //create personal folder
                    File folder = new File(Environment.getExternalStorageDirectory().toString()+ "/Android/data/BDtextbook");
                    if(!folder.exists()){
                        folder.mkdir();
                    }
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getUserInfo(FirebaseUser user) {
        String name = user.getDisplayName();
        //String email = user.getEmail();
        //String uid = "user.getUid()";
        textView.setText(String.format("Welcome %s", name));
        //update UI for admin
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Admins/"+name);
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    requestBtn.setVisibility(View.VISIBLE);
                } else{
                    requestBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void logout(View view) {
        mAuth.signOut();
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    public void openAdmin(View view) {
        startActivity(new Intent(this,AdminActivity.class));
    }
}
