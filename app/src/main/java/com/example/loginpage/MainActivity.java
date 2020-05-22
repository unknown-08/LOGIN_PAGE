package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView changableTextView;
    private TextInputLayout emailtextinputlayout,passwordtextinputlayout;
    private EditText passwordtext,emailtext;
    private Button loginButton;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#a80008"));

         actionBar.setBackgroundDrawable(colorDrawable);
         changableTextView=(TextView)findViewById(R.id.changableTextview);
         emailtextinputlayout= findViewById(R.id.teacheremaileditText);
         passwordtextinputlayout=findViewById(R.id.teacherpasswordeditText);
         emailtext=(EditText)findViewById(R.id.emailtext);
         passwordtext=(EditText)findViewById(R.id.passwordtext);
         loginButton=(Button)findViewById(R.id.teacherloginButton);
         saveLoginCheckBox = (CheckBox)findViewById(R.id.logincheckBox);
         loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
         loginPrefsEditor = loginPreferences.edit();
        /*if(saveLoginCheckBox.isChecked()){
            saveLoginCheckBox.setTextColor(Color.RED);
            saveLoginCheckBox.setBackgroundColor(Color.parseColor("#cbff75"));
        }*/

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailtext.setText(loginPreferences.getString("email", ""));
            passwordtext.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

         changableTextView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent=new Intent(MainActivity.this,TeacherLoginActivity.class);
                 startActivity(intent);
             }
         });
        auth=FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){

                    Intent intent=new Intent(MainActivity.this,AfterLogin.class);
                    startActivity(intent);
                }
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEmailAddress();
                validatePassword();
                login();
            }
        });

    }

    protected void onStart() {

        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

      private void login(){

        String email=emailtextinputlayout.getEditText().getText().toString().trim();
        String password=passwordtextinputlayout.getEditText().getText().toString().trim();

            if (saveLoginCheckBox.isChecked()) {
              loginPrefsEditor.putBoolean("saveLogin", true);
              loginPrefsEditor.putString("email", email);
              loginPrefsEditor.putString("password", password);
              loginPrefsEditor.commit();
          } else {
              loginPrefsEditor.clear();
              loginPrefsEditor.commit();
          }

          if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(getApplicationContext(),"Enter email and password",Toast.LENGTH_LONG).show();
        }
        else {

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
private boolean validateEmailAddress(){
        String email=emailtextinputlayout.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            emailtextinputlayout.setError("Email is required.Can't be empty.");
            return false;
        }
        else {
            emailtextinputlayout.setError(null);
          return   true;
        }

}
    private boolean validatePassword(){
        String password=passwordtextinputlayout.getEditText().getText().toString().trim();
        if(password.isEmpty()){
            passwordtextinputlayout.setError("Email is required.Can't be empty.");
            return false;
        }
        else {
            passwordtextinputlayout.setError(null);
            return true;
        }

    }

}
