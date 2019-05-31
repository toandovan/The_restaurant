package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapp.Common.Common;
import com.example.myapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    EditText edt_phone,edt_pass,edt_pass_1,edt_name;
    Button btn_sign_up;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_pass=(EditText)findViewById(R.id.edt_Pass);
        edt_pass_1=(EditText)findViewById(R.id.edt_Pass_again);
        edt_phone=(EditText)findViewById(R.id.edt_Phone);
        edt_name=(EditText)findViewById(R.id.edt_name);

        btn_sign_up=(Button)findViewById(R.id.btnSign_Up);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDiago = new ProgressDialog(SignUp.this);
                    mDiago.setMessage("Please waiting...");
                    mDiago.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(edt_phone.getText().toString().isEmpty()||edt_name.getText().toString().isEmpty()
                            ||edt_pass.getText().toString().isEmpty()||edt_pass_1.getText().toString().isEmpty())
                            {
                                mDiago.dismiss();
                                Toast.makeText(SignUp.this, "empty", Toast.LENGTH_SHORT).show();
                            }
                            else
                            if ((dataSnapshot.child(edt_phone.getText().toString()).exists()))
                            {
                                mDiago.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number already register", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                                    if (edt_pass.getText().toString().equals(edt_pass_1.getText().toString())) {
                                        mDiago.dismiss();

                                            User user = new User(edt_name.getText().toString(), Common.getMD5(edt_pass.getText().toString()));
                                            table_user.child(edt_phone.getText().toString()).setValue(user);
                                            Toast.makeText(SignUp.this, "sign up successfully", Toast.LENGTH_SHORT).show();
                                            Intent main = new Intent(SignUp.this, MainActivity.class);
                                            startActivity(main);

                                    } else
                                    {
                                        mDiago.dismiss();
                                        Toast.makeText(SignUp.this, "do not match pass", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(SignUp.this,"Please check your connection!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
