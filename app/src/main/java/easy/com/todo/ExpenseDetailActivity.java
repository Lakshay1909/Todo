package easy.com.todo;

/**
 * Created by Hp on 7/5/2017.
 */

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ExpenseDetailActivity extends AppCompatActivity {

    long epochtime=0l;
    String title,desc,dat,tim;
    EditText titleTextView,descriptionedittext, dateEdittext,timeedittext;
    ImageButton ib1,ib2,ib3,ib4;
    long date;
    static final int Dialogid=0;
    int hour_s,minute_s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_detail_activity);

        titleTextView = (EditText) findViewById(R.id.editText);
        dateEdittext = (EditText) findViewById(R.id.editText4);
        descriptionedittext=(EditText)findViewById(R.id.editText2);
        timeedittext = (EditText) findViewById(R.id.editText5);
        ib1=(ImageButton)findViewById(R.id.imageButton2);
        ib2=(ImageButton)findViewById(R.id.imageButton3);
        ib3=(ImageButton)findViewById(R.id.imageButton);
        ib4=(ImageButton)findViewById(R.id.imageButton4);
        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar newCalendar = Calendar.getInstance();
                int month = newCalendar.get(Calendar.MONTH);  // Current month
                int year = newCalendar.get(Calendar.YEAR);   // Current year
                int day=newCalendar.get(Calendar.DAY_OF_MONTH); //Current day
                showDatePicker(ExpenseDetailActivity.this, year, month, day);
            }
        });
        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                hour_s=calendar.get(Calendar.HOUR_OF_DAY);
                minute_s=calendar.get(Calendar.MINUTE);
                showDialog(Dialogid);
            }
        });
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                try{
                    startActivityForResult(intent,200);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ib4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                try{
                    startActivityForResult(intent,250);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button submitButton = (Button) findViewById(R.id.button);
        final Intent i = getIntent();
        title = i.getStringExtra(IntentConstants.EXPENSE_TITLE);
        desc=i.getStringExtra(IntentConstants.EXPENSE_DESCRIPTION);
        dat=i.getStringExtra(IntentConstants.EXPENSE_DATE);
        tim=i.getStringExtra(IntentConstants.EXPENSE_TIME);
        if(i.getBooleanExtra(IntentConstants.ADD_ITEM,false)==true) {

            titleTextView.setText(title);
            descriptionedittext.setText(desc);
            dateEdittext.setText(dat);
            timeedittext.setText(tim);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = titleTextView.getText().toString();
                String newdesc=descriptionedittext.getText().toString();
                String ondate=dateEdittext.getText().toString();
                String attime=timeedittext.getText().toString();
                if(newTitle.isEmpty())
                {
                    Toast.makeText(ExpenseDetailActivity.this,"title not entered",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(newdesc.isEmpty())
//                {
//                    Toast.makeText(ExpenseDetailActivity.this,"description not entered",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(ondate.isEmpty())
                {
                    Toast.makeText(ExpenseDetailActivity.this,"date not entered",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(attime.isEmpty())
                {
                    Toast.makeText(ExpenseDetailActivity.this,"time not entered",Toast.LENGTH_SHORT).show();
                    return;
                }
                epochtime=calculateepoch(ondate,attime);
                i.putExtra("Epoch",epochtime);
                if(System.currentTimeMillis()>epochtime)
                {
                    Toast.makeText(ExpenseDetailActivity.this,"time has already elapsed",Toast.LENGTH_SHORT).show();
                    return;
                }

                ExpenseOpenHelper expenseOpenHelper = new ExpenseOpenHelper(ExpenseDetailActivity.this);

                SQLiteDatabase database = expenseOpenHelper.getWritableDatabase();
                if(i.getBooleanExtra(IntentConstants.ADD_ITEM,false)==false) {
                    ContentValues cv = new ContentValues();
                    cv.put(ExpenseOpenHelper.EXPENSE_TITLE, newTitle);
                    cv.put(ExpenseOpenHelper.EXPENSE_DESCRIPTION,newdesc);
                    cv.put(ExpenseOpenHelper.EXPENSE_DATE, ondate);
                    cv.put(ExpenseOpenHelper.EXPENSE_TIME, attime);

                    database.insert(ExpenseOpenHelper.EXPENSE_TABLE_NAME, null, cv);

                    Cursor cursor = database.query(ExpenseOpenHelper.EXPENSE_TABLE_NAME,null,null,null,null,null,null);
                    cursor.moveToLast();
                    int id=cursor.getInt(cursor.getColumnIndex(ExpenseOpenHelper.EXPENSE_ID));
                    i.putExtra("id",id);
                    //  Intent i = new Intent();
                    // i.putExtra(IntentConstants.EXPENSE_TITLE, newTitle);
                    setResult(MainActivity.add, i);
                }
                else
                {
                    int id_to_change = i.getIntExtra(IntentConstants.EXPENSE_ID,-1);

                    ContentValues cv = new ContentValues();
                    cv.put(ExpenseOpenHelper.EXPENSE_TITLE, newTitle);
                    cv.put(ExpenseOpenHelper.EXPENSE_DESCRIPTION,newdesc);
                    cv.put(ExpenseOpenHelper.EXPENSE_DATE,ondate);
                    cv.put(ExpenseOpenHelper.EXPENSE_TIME,attime);

                    database.update(ExpenseOpenHelper.EXPENSE_TABLE_NAME,cv,ExpenseOpenHelper.EXPENSE_ID + " = " + id_to_change,null);
                   i.putExtra("id",id_to_change);
                    setResult(MainActivity.update,i);


                }
             //   Alarmsetter(alarmtime,id);

                finish();
            }
        });


    }

    private long calculateepoch(String date,String time)
    {  SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        long epoch=0,epoch1=0;
        final long  difference=19800000;
        try {
            Date dateepoch = sdf.parse(date);
            epoch=dateepoch.getTime();
          //  Log.i("Tag",epoch+"");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat sdf1=new SimpleDateFormat("HH:mm");
        try
        {
            Date timeepoch=sdf1.parse(time);
            epoch1=timeepoch.getTime();
          //  Log.i("Tag",epoch1+"");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        long epochtime=epoch+epoch1+difference;
       // Log.i("Tag",epochtime+"");
        return epochtime;
    }

    protected Dialog onCreateDialog(int id)
    {
        if(id==Dialogid)
            return new TimePickerDialog(ExpenseDetailActivity.this,timelistner,hour_s,minute_s,false);
        return  null;
    }
    protected  TimePickerDialog.OnTimeSetListener timelistner= new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_s=hourOfDay;
            minute_s=minute;
            timeedittext.setText(hour_s + ":" + minute_s);
        }
    };
    public void showDatePicker(Context context, int initialYear, int initialMonth, int initialDay) {

        // Creating datePicker dialog object
        // It requires context and listener that is used when a date is selected by the user.

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    //This method is called when the user has finished selecting a date.
                    // Arguments passed are selected year, month and day
                    @Override
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {

                        // To get epoch, You can store this date(in epoch) in database
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        date = calendar.getTime().getTime();
                        // Setting date selected in the edit text
                        dateEdittext.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, initialYear, initialMonth, initialDay);

        //Call show() to simply show the dialog
        datePickerDialog.show();

    }
    @Override
    public void onBackPressed () {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==200)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> text=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                titleTextView.setText(text.get(0));
            }
        }
        if(requestCode==250)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> desc=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                descriptionedittext.setText(desc.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


