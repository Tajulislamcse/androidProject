package com.example.sadmansakib.todo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class NewToDo extends AppCompatActivity {
    private int HOUR,MIN,DAY,MONTH,YEAR;
    private String AMorPM;
    private Calendar cal,current;
    private TextView dateTV,timeTV;
    private EditText titleET,detailsET;
    private Button saveORupdate;
    private Switch notification_switch;
    private int notification_state;
    DatabaseHandler db;
    boolean editToDo=false;
    int ID_TABLE, id_max,time_offset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);


        db = new DatabaseHandler(this);

        titleET=(EditText) findViewById(R.id.TITLE);
        detailsET=(EditText) findViewById(R.id.DETAILS);
        notification_switch=(Switch)findViewById(R.id.notification_switch);

        cal= Calendar.getInstance(Locale.getDefault());
        current = Calendar.getInstance(Locale.getDefault());

//        TimeZone timeZone = cal.getTimeZone();
//        int mGMTOffset = timeZone.getRawOffset();

//        time_offset= (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
//        Log.d("***UTC:", String.valueOf(time_offset));
//        if(time_offset==6) time_offset=0;
//        else if(time_offset==0) time_offset=6;

        time_offset=0;

        HOUR=cal.get(Calendar.HOUR_OF_DAY);
        MIN=cal.get(Calendar.MINUTE);
        DAY=cal.get(Calendar.DAY_OF_MONTH);
        MONTH=cal.get(Calendar.MONTH);
        YEAR=cal.get(Calendar.YEAR);

        dateTV=(TextView)findViewById(R.id.DateText);
        timeTV=(TextView)findViewById(R.id.TimeText);

        saveORupdate=(Button)findViewById(R.id.SaveOrUpdateButton) ;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Log.d("***CURR:(-GMT)", + HOUR +":"+MIN);
        if(extras != null)
        {
            editToDo=true;

            ID_TABLE = extras.getInt("ID_TABLE");
            String title,details,date,time;
            title=extras.getString("TITLE");
            details=extras.getString("DETAILS");
            date=extras.getString("DATE");
            time=extras.getString("TIME");
            notification_state=extras.getInt("NOTIFICATION_STATE");
            //Log.d("ID_TABLE:::", String.valueOf(ID_TABLE));
            titleET.setText(title);
            detailsET.setText(details);
            dateTV.setText(date);
            timeTV.setText(time);
            saveORupdate.setText("UPDATE");
            Log.d("Noti-state-EDIT:", String.valueOf(notification_state));
            if(notification_state==1) notification_switch.setChecked(true);
            else notification_switch.setChecked(false);
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void showTimePickerDialog(View view) {
        TimePickerDialog timePickerDialog=new TimePickerDialog(this,timeSetListener,HOUR,MIN,false);
        timePickerDialog.show();

    }

    private TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("***timeOfDay:"+hourOfDay+":"+minute,"-");
            //Toast.makeText(NewToDo.this,hourOfDay+":"+minute,Toast.LENGTH_LONG).show();
            if(hourOfDay>12)
            {
                HOUR=hourOfDay;
                MIN=minute;
                AMorPM="PM";
                timeTV.setText((HOUR-12)+":"+MIN +" "+AMorPM);
                //Toast.makeText(NewToDo.this,(HOUR-12)+":"+MIN +" "+AMorPM,Toast.LENGTH_LONG).show();
            }
            else if(hourOfDay==0)
            {
                HOUR=hourOfDay;
                MIN=minute;
                AMorPM="AM";
                timeTV.setText("12:"+MIN+" "+AMorPM);
               // Toast.makeText(NewToDo.this,"12:"+MIN+" "+AMorPM,Toast.LENGTH_LONG).show();
            }
            else if(hourOfDay==12)
            {
                HOUR=hourOfDay;
                MIN=minute;
                AMorPM="PM";
                timeTV.setText("12:"+MIN +" "+AMorPM);
                //Toast.makeText(NewToDo.this,"12:"+MIN +" "+AMorPM,Toast.LENGTH_LONG).show();
            }

            else
            {
                HOUR=hourOfDay;
                MIN=minute;
                AMorPM="AM";
                timeTV.setText(HOUR+":"+MIN +" "+AMorPM);
               // Toast.makeText(NewToDo.this,HOUR+":"+MIN +" "+AMorPM,Toast.LENGTH_LONG).show();
            }
        }
    };

    public void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog=new DatePickerDialog(this,dateSetListener,YEAR,MONTH,DAY);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            YEAR=year;
            MONTH=month+1;
            DAY=dayOfMonth;
            //Toast.makeText(NewToDo.this,DAY+ "/"+MONTH+"/"+YEAR,Toast.LENGTH_LONG).show();
            dateTV.setText(DAY+ "/"+MONTH+"/"+YEAR);
        }
    };

    public void saveToDo(View view) {
        int validation = 1;
        if (titleET.getText().toString().trim().isEmpty()) {
            titleET.setError("This field is required !");
            validation = 0;
        }
        if (dateTV.getText().toString().equals("Choose Date")) {
            dateTV.setError("This field is required !");
            validation = 0;
        }
        if (timeTV.getText().toString().equals("Choose Time")) {
            timeTV.setError("This field is required !");
            validation = 0;
        }
        if(validation==1){
            int noti_state;
            if (notification_switch.isChecked()) noti_state = 1;
            else noti_state = 0;

            //Save New ToDo
            if (!editToDo) {
                Log.d("Noti-state-SET:", String.valueOf(noti_state));
                boolean status = db.addToDo(new ToDo(titleET.getText().toString(), detailsET.getText().toString(), dateTV.getText().toString(), timeTV.getText().toString(), noti_state));
                if (status) {

                    if (notification_switch.isChecked()) {
                        id_max = db.getLastID();
    //                Log.d("***IDMAX", String.valueOf(id_max));
                        cal.set(Calendar.MONTH, MONTH - 1);
                        cal.set(Calendar.YEAR, YEAR);
                        cal.set(Calendar.DAY_OF_MONTH, DAY);

                        cal.set(Calendar.HOUR_OF_DAY, HOUR - time_offset);
                        cal.set(Calendar.MINUTE, MIN);
                        cal.set(Calendar.SECOND, 0);
                        //cal.set(YEAR, MONTH,DAY,HOUR,MINUTE);

                        if (cal.compareTo(current) <= 0) {
                            //The set Date/Time already passed
                            //As time has already passed so, turn off the notification
                            db.addToDo(new ToDo(titleET.getText().toString(), detailsET.getText().toString(), dateTV.getText().toString(), timeTV.getText().toString(), 0));
                            Toast.makeText(getApplicationContext(), "Invalid Date/Time. Failed to set Notification !", Toast.LENGTH_LONG).show();
                            Log.d("***ALARM:", "Invalid Date/Time" + cal.getTime() + current.getTime());
                        } else {
                            setAlarm(cal);
                            Toast.makeText(this, "ToDo Saved. Notification set .", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, " ToDo Saved. Notification off", Toast.LENGTH_LONG).show();
                    }
                } else Toast.makeText(this, "Failed to save !!!", Toast.LENGTH_LONG).show();
            }

            //Edit a ToDo
            else {
                boolean status = db.updateToDo(new ToDo(ID_TABLE, titleET.getText().toString(), detailsET.getText().toString(), dateTV.getText().toString(), timeTV.getText().toString(), noti_state));
                if (status) {
                    id_max = ID_TABLE;
                    if (notification_switch.isChecked()) {
    //                Log.d("***IDMAX", String.valueOf(id_max));
                        String D, M, Y;
                        String h, m, a;
                        String TV_TIME = (String) timeTV.getText();
                        String TV_DATE = (String) dateTV.getText();
                        Log.d("***TV_TIME:", TV_TIME);
                        Log.d("***TV_DATE:", TV_DATE);
                        String time[] = TV_TIME.split(":");
                        String date[] = TV_DATE.split("/");

                        D = date[0];
                        M = date[1];
                        Y = date[2];
                        h = time[0];
                        m = time[1];

                        String split_min[] = m.split(" ");
                        m = split_min[0];
                        a = split_min[1];

                        if (Integer.parseInt(h) == 12 && a.equals("AM")) {
                            Log.d("***YES H=12 AM:" + h, " ");
                            h = "0";
                        } else if (Integer.parseInt(h) == 12 && a.equals("PM")) {
                            Log.d("***YES H=12 PM:" + h, " ");
                            h = "12";
                        } else if (a.equals("PM")) {
                            Log.d("***YES HR PM:" + h, " ");
                            int h2 = Integer.parseInt(h) + 12;
                            h = String.valueOf(h2);
                        }


                        cal.set(Calendar.MONTH, Integer.parseInt(M) - 1);
                        cal.set(Calendar.YEAR, Integer.parseInt(Y));
                        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(D));

                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h) - time_offset);
                        cal.set(Calendar.MINUTE, Integer.parseInt(m));
                        cal.set(Calendar.SECOND, 0);

                        //Log.d("***"+D+"/"+M+"/"+Y, "-");
                        //Log.d("***"+h+":"+m+" "+a, "-");
                        //cal.set(YEAR, MONTH,DAY,HOUR,MINUTE);

                        if (cal.compareTo(current) <= 0) {
                            //The set Date/Time already passed
                            //As time has already passed so, turn off the notification
                            db.updateToDo(new ToDo(ID_TABLE, titleET.getText().toString(), detailsET.getText().toString(), dateTV.getText().toString(), timeTV.getText().toString(), 0));
                            Toast.makeText(getApplicationContext(), "Invalid Date/Time. Failed to set Notification !", Toast.LENGTH_LONG).show();
                            Log.d("***ALARM:", "Invalid Date/Time" + cal.getTime() + current.getTime());
                        } else {
                            setAlarm(cal);
                            Toast.makeText(this, "ToDo Updated. Notification set .", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id_max, intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        Toast.makeText(this, " ToDo Updated. Notification off .", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, " Not Updated!!!", Toast.LENGTH_LONG).show();
                }

            }

            Intent intent = new Intent(NewToDo.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Please fill up required fields !", Toast.LENGTH_LONG).show();
        }

    }
    private void setAlarm(Calendar targetCal){
        Log.d("***ALARM:","Alarm is set@ " + targetCal.getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm a");
        String current_datetime = simpleDateFormat.format(new Date());
        String target_datetime = simpleDateFormat.format(targetCal.getTime());
        Toast.makeText(this,  "Alarm is set at " + target_datetime, Toast.LENGTH_SHORT).show();
        Log.d( "***CURRENT:" , current_datetime);
        Log.d( "***TARGET:" , target_datetime);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("ID",id_max);
        intent.putExtra("TITLE",titleET.getText().toString());
        intent.putExtra("DETAILS",detailsET.getText().toString());
        Log.d("***IDMAX:", String.valueOf(id_max));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),id_max, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Log.d("***PRESENT in MILL:","Alarm is set@ " + System.currentTimeMillis());
        long alarmTime= targetCal.getTimeInMillis();
        //Log.d("***ALARM in MILL:","Alarm is set@ " + alarmTime);

        alarmManager.setExact(AlarmManager. RTC_WAKEUP, alarmTime, pendingIntent);
    }



}


