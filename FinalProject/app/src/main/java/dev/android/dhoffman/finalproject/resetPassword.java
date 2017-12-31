package dev.android.dhoffman.finalproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class resetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

    }

//    protected static String savePassword;
//    @Override
//    protected void onDestroy(){
//        Log.i("Reset","Project Destroyed");
//        super.onDestroy();
//        savePassword=Login.currentPassword;
//        Log.i("Destroy", "Save Password is " + savePassword);
//        Log.i("Destroy", "Current Password is " + Login.currentPassword);
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        savePassword=Login.currentPassword;
//        Log.i("Pause", "Save Password is " + savePassword);
//        Log.i("Pause", "Current Password is " + Login.currentPassword);
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        Login.currentPassword=savePassword;
//        Log.i("Resume", "Save Password is " + savePassword);
//        Log.i("Resume", "Current Password is " + Login.currentPassword);
//    }

    public String getCurrentUsername(){
        //read the current username from the Login.java class
        return Login.currentUsername;
    }

    public void onReset(View view) {
        if (getCurrentUsername().equals("admin")) {
            //Get user input for password reset
            EditText password = findViewById(R.id.enterPassword);
            String newPassword = password.getText().toString();
            Login.currentPassword=newPassword;
            //output message to screen
            Context context = getApplicationContext();
            CharSequence text = "password for admin is now changed to " + newPassword;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            //output message to screen
            Context context = getApplicationContext();
            CharSequence text = "password for admin was not changed";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void onNext(View view){
        finish();
    }
}
