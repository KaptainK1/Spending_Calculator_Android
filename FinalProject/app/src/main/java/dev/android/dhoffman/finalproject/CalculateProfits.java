package dev.android.dhoffman.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static dev.android.dhoffman.finalproject.MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_AMOUNT;

public class CalculateProfits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_profits);
        //set original year value to this year
        final Calendar cal = GregorianCalendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String strYear = String.valueOf(year);
        EditText setYear = findViewById(R.id.year);
        setYear.setText(strYear);
    }

    @Override
    protected void onDestroy(){
        Log.i("Calculate Profits","Project Destroyed");
        super.onDestroy();
    }

    protected String getMonth;
    protected String getYear;
    protected double dbPay = 0.00d;
    private double dbProfit=0.00d;
    protected double dbSalary = 0.00d;

    private void setDisplay(String str) {
        TextView view = findViewById(R.id.savings);
        view.setText(str);
    }

    public void onReturn(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void onDelete(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void onFind(View view){
        Intent intent = new Intent(this, Lookup.class);
        startActivity(intent);
    }

    public void showMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        double dbExtraSalary;
        EditText salary = findViewById(R.id.salary);
        EditText extraIncome = findViewById(R.id.extra);
        //check to see if a salary was entered
        if (salary.getText().toString().equals("") || extraIncome.getText().toString().equals("")){
            showMessage("Please enter a valid salary or Extra $");
            onCleanup(view);
        } else  {
            dbSalary = Double.parseDouble(salary.getText().toString());
            dbExtraSalary = Double.parseDouble(extraIncome.getText().toString());
            // Check which radio button was clicked then do the math
            switch(view.getId()) {
                case R.id.radio_weekly:
                    if (checked)
                        dbPay=(dbSalary*4) + dbExtraSalary;
                    break;
                case R.id.radio_biweekly:
                    if (checked)
                        dbPay=(dbSalary*2) + dbExtraSalary;
                    break;
                case R.id.radio_monthly:
                    if (checked)
                        dbPay=dbSalary + dbExtraSalary;
                    break;
            }
        }
    }

    public void onCleanup(View view){
        //clear radio checks after running calculation
        RadioGroup rb = findViewById(R.id.group);
        rb.clearCheck();
    }

    public void returnProfit(View view){
        // need to check if any of the buttons are checked
        RadioButton btnWeekly = findViewById(R.id.radio_weekly);
        RadioButton btnBiWeekly = findViewById(R.id.radio_biweekly);
        RadioButton btnMonthly = findViewById(R.id.radio_monthly);
        boolean weeklyChecked = btnWeekly.isChecked();
        boolean biWeeklyChecked = btnBiWeekly.isChecked();
        boolean monthlyChecked = btnMonthly.isChecked();
        //if no buttons are checked, display a message to the user
        if (!weeklyChecked && !biWeeklyChecked && !monthlyChecked) {
            TextView savings = findViewById(R.id.savings);
            savings.setText("");
            int color = getResources().getColor(R.color.colorProjectBackground);
            savings.setBackgroundColor(color);
            isFinishing();
            showMessage("Choose a Salary Calculation");
            return;
        }

        EditText month = findViewById(R.id.month);
        getMonth = month.getText().toString();
        //Need to Minus one from the query, as months are from 0-11, not 1-12
        int addMonth = Integer.valueOf(getMonth) - 1;
        getMonth = String.valueOf(addMonth);
        EditText year = findViewById(R.id.year);
        getYear = year.getText().toString();

        MainActivity.budgetDbHelper dbHelper = new MainActivity.budgetDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double dbPrice;
        double dbTotal = 0.00d;
        String selectQuery = "SELECT " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_AMOUNT + " FROM " + MainActivity.dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH + " =" + getMonth + " AND " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + getYear;
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                dbPrice = c.getDouble(c.getColumnIndex(COLUMN_NAME_AMOUNT));
                dbTotal += dbPrice;
                Log.i("Total Spent", String.valueOf(dbTotal));
            } while (c.moveToNext());
            dbProfit = dbPay - dbTotal;
            Log.i("Salary Entered", String.valueOf(dbPay));
            String strProfit = Double.toString(dbProfit);
            setDisplay("$" + strProfit);
            TextView savings = findViewById(R.id.savings);
            if (dbProfit >= 0) {
                savings.setBackgroundColor(Color.GREEN);
            } else {
                savings.setBackgroundColor(Color.RED);
            }
            c.close();
            onCleanup(view);
        } else {
            showMessage("No receipts entered for selected time period");
        }
    }
}