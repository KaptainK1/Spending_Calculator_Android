package dev.android.dhoffman.finalproject;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Lookup extends AppCompatActivity {

    protected String strMonth = "";
    protected String strYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);
        //set original year value to this year
        final Calendar c = GregorianCalendar.getInstance();
        int year = c.get(Calendar.YEAR);
        String strYear = String.valueOf(year);
        EditText setYear = findViewById(R.id.year);
        setYear.setText(strYear);
    }

    public void showMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onReturn(View view){
        finish();
    }

    public void onFind(View view){
        //get the values for the year and month the user selected
        EditText month = findViewById(R.id.month);
        strMonth=month.getText().toString();
        EditText year = findViewById(R.id.year);
        strYear=year.getText().toString();
        TableLayout table = findViewById(R.id.table);
        //Need to remove previous searches by dropping all the rows
        table.removeAllViews();

        if(strMonth.equals("") ||  strYear.equals("")){
            showMessage("Please Enter a Valid Month and Year");
            return;
        } else {
            //Need to Minus one from the query, as months are from 0-11, not 1-12
            int addMonth = Integer.valueOf(strMonth) - 1;
            strMonth = String.valueOf(addMonth);
        }

        MainActivity.budgetDbHelper dbHelper = new MainActivity.budgetDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //using a select statement to see how many receipts were for a given month and year
        //order by the date of purchase
        String selectQuery = ("SELECT *" + " FROM " + MainActivity.dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH +
                " =" + strMonth + " AND " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + strYear +
                " ORDER BY " + "CAST(" +MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " AS INTEGER)" + " ASC");

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //do loop to handle each return of the select query
            do {
                //add the three necessary columns to return in a String array
                String strReceipt[] = {"$ "+cursor.getString(1), cursor.getString(4),cursor.getString(3)};
                //add a new row to the table and set the params
                TableRow row = new TableRow(this);
                TableRow.LayoutParams layout = new TableRow.LayoutParams(MATCH_PARENT, WRAP_CONTENT,3);
                row.setLayoutParams(layout);
                table.addView(row);
                //for loop to add each value of the string array to its own textview
                for (int i = 0; i<strReceipt.length; i++) {
                    //create the textview for the row created above and set the params
                    TextView textView = new TextView(this);
                    textView.setLayoutParams( new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1));
                    textView.setGravity(Gravity.LEFT);
                    textView.setPadding(60,0,0,0);
                    textView.setText(strReceipt[i]);
                    row.addView(textView);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            showMessage("No receipts entered for selected time period");
        }
    }
}