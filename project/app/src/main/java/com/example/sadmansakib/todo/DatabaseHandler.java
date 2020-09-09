package com.example.sadmansakib.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.id;

public class DatabaseHandler extends SQLiteOpenHelper{
    private static final int DB_VERSION=1;
    private static final String DB_NAME="ToDoDB.db";
    private static final String TABLE_NAME="ToDo";


    private static final String COL_ID="id";
    private static final String COL_TITLE="title";
    private static final String COL_DETAILS="details";
    private static final String COL_DATE="date";
    private static final String COL_TIME="time";
    private static final String COL_NOTIFICATION_STATE="notification_state";

    private static final String QUERY_CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_TITLE+" text,"+COL_DETAILS+" text,"+
                                        COL_DATE+" text,"+COL_TIME+" text,"+COL_NOTIFICATION_STATE+" INTEGER"+");";

    public DatabaseHandler(Context context) {

        super(context, DB_NAME, null, DB_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d("###onCreate: ", "onCreate .......");
        db.execSQL(QUERY_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean addToDo(ToDo toDo)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_TITLE,toDo.getTitle());
        contentValues.put(COL_DETAILS,toDo.getDetails());
        contentValues.put(COL_DATE,toDo.getDate());
        contentValues.put(COL_TIME,toDo.getTime());
        Log.d("Noti-state-INSERT:", String.valueOf(toDo.getNotification_state()));
        contentValues.put(COL_NOTIFICATION_STATE,toDo.getNotification_state());

        long id=database.insert(TABLE_NAME,null,contentValues);
        database.close();

        if(id>0) return true;
        else return false;


    }

    //Single Row
//    public ToDo getToDo (int id)
//    {
//        SQLiteDatabase database=this.getReadableDatabase();
//        Cursor cursor=database.query(TABLE_NAME,null,null,null,null,null,null);
//
//        if(cursor!=null)
//            cursor.moveToFirst();
//
//        ToDo toDo=new ToDo(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getExtras(4));
//        return toDo;
//
//
//    }

    public int getLastID()
    {
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MAX("+COL_ID+") FROM "+TABLE_NAME, null);
        int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        return maxid;
    }

    public ArrayList<ToDo> getAllToDo()
    {
        ArrayList<ToDo> toDoList=new ArrayList<>();
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.query(TABLE_NAME,null,null,null,null,null,null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {

                int id=cursor.getInt(cursor.getColumnIndex(COL_ID));
                String title=cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String details=cursor.getString(cursor.getColumnIndex(COL_DETAILS));
                String date=cursor.getString(cursor.getColumnIndex(COL_DATE));
                String time=cursor.getString(cursor.getColumnIndex(COL_TIME));
                int notification_state=cursor.getInt(cursor.getColumnIndex(COL_NOTIFICATION_STATE));
                ToDo toDo=new ToDo(id,title,details,date,time,notification_state);
                toDoList.add(toDo);
                cursor.moveToNext();
            }

        }
        cursor.close();
        database.close();
        return  toDoList;

    }

    public void deleteToDo(ToDo toDo)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+COL_ID+" = "+toDo.getId());
        sqLiteDatabase.close();

    }

    public boolean updateToDo(ToDo toDo)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        //sqLiteDatabase.execSQL("UPDATE "+TABLE_NAME+" SET "+COL_TITLE+" = "+toDo.getTitle()+" , "+COL_DETAILS+" = "+toDo.getDetails()
        //    +" , "+COL_DATE+" = "+toDo.getDate()+" , "+COL_TIME+" = "+toDo.getTime()+" WHERE "+COL_ID+" = "+toDo.getId()+";");

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, toDo.getTitle());
        values.put(COL_DETAILS, toDo.getDetails());
        values.put(COL_DATE, toDo.getDate());
        values.put(COL_TIME, toDo.getTime());
        values.put(COL_NOTIFICATION_STATE, toDo.getNotification_state());

        // updating row
        int status= sqLiteDatabase.update(TABLE_NAME, values, COL_ID + " = "+toDo.getId(),null );
        sqLiteDatabase.close();

        if(status>0) return true;
        else return false;


    }

    public void toogleAlarm(ToDo toDo) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NOTIFICATION_STATE, toDo.getNotification_state());

        // updating row
        sqLiteDatabase.update(TABLE_NAME, values, COL_ID + " = "+toDo.getId(),null );
        sqLiteDatabase.close();

    }
}
