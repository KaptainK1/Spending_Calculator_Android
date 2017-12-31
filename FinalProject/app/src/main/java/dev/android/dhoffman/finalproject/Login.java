package dev.android.dhoffman.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        currentPassword=resetPassword.savePassword;
//        Log.i("Create", "Save Password is " + resetPassword.savePassword);
//        Log.i("Create", "Current Password is " + currentPassword);
    }

//    @Override
//    protected void onDestroy(){
//        Log.i("Login","Project Destroyed");
//        super.onDestroy();
//        currentPassword=resetPassword.savePassword;
//    }

    //set the username and password to login. This could be replaced with a DB that would keep all usernames and passwords
    protected static String currentPassword="password";
    protected static String currentUsername = "admin";

    public void onNext(View view) {
        //get both entered values
        EditText edit = findViewById(R.id.enterPassword);
        EditText editUsername = findViewById(R.id.enterUsername);
        String strUsername = editUsername.getText().toString();
        String strPassword = edit.getText().toString();
        Log.i("Login", currentPassword);

        if(strPassword.equals(currentPassword) && strUsername.equals(currentUsername)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Entered Wrong Password";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

//    public void onReset(View view){
//        Intent intent = new Intent(this,resetPassword.class);
//        startActivity(intent);
//    }

    public void onReset(View view){
        Intent intent = new Intent(this, resetPassword.class);
        startActivity(intent);
    }
}
