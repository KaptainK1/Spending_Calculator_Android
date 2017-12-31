package dev.android.dhoffman.finalproject;
//Added functionality for this activity to the main activity instead

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static dev.android.dhoffman.finalproject.MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_AMOUNT;

public class DeleteReceipts extends AppCompatActivity {

    protected static String strDay;
    protected static String strMonth;
    protected static String strYear;
    protected static String strPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_receipts);
    }

    @Override
    protected void onDestroy(){
        Log.i("Delete Receipts","Project Destroyed");
        super.onDestroy();
    }

    public void showMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onCalc(View view) {
        Intent intent = new Intent(this, CalculateProfits.class);
        startActivity(intent);
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void onFind(View view) {
        Intent intent = new Intent(this, Lookup.class);
        startActivity(intent);
    }


    public void onDelete(View view) {

        EditText day = findViewById(R.id.day);
        strDay=day.getText().toString();
        EditText month = findViewById(R.id.month);
        strMonth=month.getText().toString();
        EditText year = findViewById(R.id.year);
        strYear=year.getText().toString();
        EditText place = findViewById(R.id.place);
        strPlace = place.getText().toString().toUpperCase().trim();

        if(strDay.equals("") || strMonth.equals("") || strPlace.equals("") || strYear.equals("")){
            showMessage("Please Enter a Valid Day,Month,Year and or Place");
            return;
        } else {
            //Need to Minus one from the query, as months are from 0-11, not 1-12
            int addMonth = Integer.valueOf(strMonth) - 1;
            strMonth = String.valueOf(addMonth);
        }

        MainActivity.budgetDbHelper dbHelper = new MainActivity.budgetDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //using a select statement to see how many receipts were deleted
        String selectQuery = ("SELECT *" + " FROM " + MainActivity.dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH +
                " =" + strMonth + " AND " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + strYear +
                " AND " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " =" + strDay +
                " AND " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE + " ='" + strPlace + "'");

        Cursor cursor = db.rawQuery(selectQuery, null);
        int deleteRows = 0;
        if (cursor.moveToFirst()) {
            do {
                deleteRows++;
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            showMessage("No receipts entered for selected time period");
        }

        String deleteQuery = ("DELETE" + " FROM " + MainActivity.dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH +
                " =" + strMonth + " AND " +
                MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + strYear +
                " AND " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " =" + strDay +
                " AND " + MainActivity.dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE + " ='" + strPlace + "'");
        Cursor c = db.rawQuery(deleteQuery, null);
        c.moveToFirst();
        c.close();
        showMessage( deleteRows + " Receipt(s) Deleted");
    }
}
