package dev.android.dhoffman.finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    protected static String strYear = "";
    protected static String strMonth = "";
    protected static String strDay = "";
    protected String strPlace = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        Log.i("Main","Project Destroyed");
        super.onDestroy();
    }

    //create the date picker class and listener
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = GregorianCalendar.getInstance();
            int year = c.get(Calendar.YEAR);
            Log.i("YEAR", String.valueOf(year));
            int month = c.get(Calendar.MONTH);
            Log.i("Month", String.valueOf(month));
            int day = c.get(Calendar.DAY_OF_MONTH);
            Log.i("DAY", String.valueOf(day));
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),this,year,month,day);
        }
        //set values to what date is picked
        public void onDateSet(DatePicker view, int year, int month, int day) {
            strYear=String.valueOf(year);
            strMonth=String.valueOf(month);
            strDay=String.valueOf(day);
        }
    }

    //When the button is pressed show the calender to pick a date
    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"datepicker");
    }

    public void showMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static final class dbBudget {
        public dbBudget() {}

        /* Inner class that defines the table contents */
        public static class dbBudgetEntry implements BaseColumns {
            public static final String TABLE_NAME = "budget";
            public static final String _ID = "id";
            public static final String COLUMN_NAME_AMOUNT = "amount";
            public static final String COLUMN_NAME_DAY = "day";
            public static final String COLUMN_NAME_MONTH = "month";
            public static final String COLUMN_NAME_YEAR = "year";
            public static final String COLUMN_NAME_PLACE = "place";
        }
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + dbBudget.dbBudgetEntry.TABLE_NAME + " (" +
                    dbBudget.dbBudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " TEXT," +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " TEXT," +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE + " TEXT," +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbBudget.dbBudgetEntry.TABLE_NAME;


    public static class budgetDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 13;
        private static final String DATABASE_NAME = "budget.db";

        protected budgetDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            //When the data base is created, execute the query defined to create the table
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //when the DB is upgraded, execute the query to delete all data, then recreate the table
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void insertBill(View view){
        budgetDbHelper dbHelper = new budgetDbHelper(getApplicationContext());
        EditText billAmount = findViewById(R.id.amount);
        String strBill= billAmount.getText().toString();

        //check to make sure a receipt amount is entered
        if (strBill.equals("")) {
            showMessage("Please Enter A Receipt Amount ");
            return;
        }

        double dbBill = Double.parseDouble(strBill);
        //check to make sure a decimal is typed
        if (Double.isNaN(dbBill)) {
            showMessage("Receipt must be a numerical decimal type");
        } else {
            BigDecimal money = new BigDecimal(dbBill);
            money = money.setScale(2, RoundingMode.HALF_DOWN);
            dbBill=money.doubleValue();
        }

        EditText editPlace = findViewById(R.id.place);
        strPlace = editPlace.getText().toString().toUpperCase().trim();

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(dbBudget.dbBudgetEntry.COLUMN_NAME_AMOUNT, dbBill);
        values.put(dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH, strMonth);
        values.put(dbBudget.dbBudgetEntry.COLUMN_NAME_DAY, strDay);
        values.put(dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE, strPlace);
        values.put(dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR, strYear);
        //Check to make sure data is entered
        if (strBill.equals("") || strMonth.equals("") || strPlace.equals("")) {
            showMessage("Enter a valid Amount, Date, and Place");
        } else {
            long newRowId =
                    db.insert(dbBudget.dbBudgetEntry.TABLE_NAME, null, values);
            isFinishing();
            showMessage("Receipt Added");
        }
    }

    public void onCalculate(View view){
        Intent intent = new Intent(this,CalculateProfits.class);
        startActivity(intent);
    }

    public void onDelete(View view){
            EditText place = findViewById(R.id.place);
            strPlace = place.getText().toString().toUpperCase().trim();

            if(strDay.equals("") || strMonth.equals("") || strPlace.equals("") || strYear.equals("")) {
                showMessage("Please Enter a Valid Day,Month,Year and or Place");
                return;
            }

            budgetDbHelper dbHelper = new budgetDbHelper(getApplicationContext());
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //using a select statement to see how many receipts were deleted
            String selectQuery = ("SELECT *" + " FROM " + dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH +
                    " =" + strMonth + " AND " +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + strYear +
                    " AND " + dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " =" + strDay +
                    " AND " + dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE + " ='" + strPlace + "'");

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

            String deleteQuery = ("DELETE" + " FROM " + dbBudget.dbBudgetEntry.TABLE_NAME + " WHERE " +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_MONTH +
                    " =" + strMonth + " AND " +
                    dbBudget.dbBudgetEntry.COLUMN_NAME_YEAR + " =" + strYear +
                    " AND " + dbBudget.dbBudgetEntry.COLUMN_NAME_DAY + " =" + strDay +
                    " AND " + dbBudget.dbBudgetEntry.COLUMN_NAME_PLACE + " ='" + strPlace + "'");
            Cursor c = db.rawQuery(deleteQuery, null);
            c.moveToFirst();
            c.close();
            showMessage( deleteRows + " Receipt(s) Deleted");
        }
    public void onFind(View view){
        Intent intent = new Intent(this,Lookup.class);
        startActivity(intent);
    }
}
