package com.example.sadmansakib.todo;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder>{

    //**************HOLDER CLASS ****************
    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title,details,date,time;
        private Button deleteItemButton,editIteamButton;
        private ImageButton alarmButton;
        private int id;
        private View container;

        //int noti_state;


        public MyViewHolder(final View itemView) {
            super(itemView);


            //noti_state=-1;
            title=(TextView)itemView.findViewById(R.id.TITLE);
            details=(TextView)itemView.findViewById(R.id.DETAILS);
            date=(TextView)itemView.findViewById(R.id.DATE);
            time=(TextView)itemView.findViewById(R.id.TIME);
            container=itemView.findViewById(R.id.SINGLEROW);
            deleteItemButton=(Button) itemView.findViewById(R.id.deleteTodoButton);
            editIteamButton=(Button) itemView.findViewById(R.id.editToDoButton);
            alarmButton=(ImageButton) itemView.findViewById(R.id.alarmButton);

        }

    }

    //******************************************




    private List<ToDo> toDoList;
    DatabaseHandler db;
    private AlertDialog.Builder alert;
    private RecyclerView recyclerView;
    private ToDoAdapter toDoAdapter;
    Context context;
    public ToDoAdapter(List<ToDo> toDoList,DatabaseHandler databaseHandler,Context context,RecyclerView recyclerView,ToDoAdapter toDoAdapter)
    {
        this.toDoList=toDoList;
        this.db=databaseHandler;
        this.context=context;
        alert=new  AlertDialog.Builder(context);
        this.recyclerView=recyclerView;
        this.toDoAdapter=toDoAdapter;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ToDo toDo= toDoList.get(position);
        final int ID_TABLE=holder.id=toDo.getId();
        holder.title.setText(toDo.getTitle());
        holder.details.setText(toDo.getDetails());
        holder.date.setText(toDo.getDate());
        holder.time.setText(toDo.getTime());
        if(toDo.getNotification_state()==1)
        {
            holder.alarmButton.setImageResource(R.mipmap.ic_alarm);
        }
        else{
            holder.alarmButton.setImageResource(R.mipmap.ic_alarm_off);
        }

        //holder.noti_state=toDo.getNotification_state();
        Log.d("Noti-state-HOLDER:", String.valueOf(toDo.getNotification_state()));


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            ToDo toDo=toDoList.get(position);
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Item Clicked "+position+" "+toDo.getTitle(),Toast.LENGTH_SHORT).show();
//            }
//        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(context,"Item Long Clicked "+position+" "+toDo.getTitle(),Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        //TODO:Cancel button click korleo noti state change hoi !
        holder.alarmButton.setOnClickListener(new View.OnClickListener() {
            private Calendar cal,current;
            int alaramState,time_offset=0,id;
            @Override
            public void onClick(View v) {
                id=toDo.getId();

                if(toDo.getNotification_state()==1)  {
                    alaramState=1;
                    alert.setTitle("Turn OFF notification !");

                }
                else {
                    alaramState=0;
                    alert.setTitle("Turn ON notification !");

                }
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Alarm is ON now . So turn it off
                        if(alaramState==1)
                        {
                            toDo.setNotification_state(0);
                            db.toogleAlarm(toDo);
                            cancelAlarm(toDo.getId());
                        }
                        //Alarm is OFF now . So turn it ON
                        else{
                            toDo.setNotification_state(1);
                            cal= Calendar.getInstance(Locale.getDefault());
                            current = Calendar.getInstance(Locale.getDefault());
                            String D,M,Y;
                            String h,m,a;
                            String TV_TIME= toDo.getTime();
                            String TV_DATE= toDo.getDate();
                            Log.d("***TV_TIME:",TV_TIME);
                            Log.d("***TV_DATE:",TV_DATE);
                            String time[]=TV_TIME.split(":");
                            String date[]=TV_DATE.split("/");

                            D=date[0];
                            M=date[1];
                            Y=date[2];
                            h=time[0];
                            m=time[1];

                            String split_min[]=m.split(" ");
                            m=split_min[0];
                            a=split_min[1];

                            if(Integer.parseInt(h)==12 && a.equals("AM"))
                            {
                                Log.d("***YES H=12 AM:"+h," ");
                                h="0";
                            }
                            else if(Integer.parseInt(h)==12 && a.equals("PM"))
                            {
                                Log.d("***YES H=12 PM:"+h," ");
                                h="12";
                            }
                            else if(a.equals("PM"))
                            {
                                Log.d("***YES HR PM:"+h," ");
                                int h2=Integer.parseInt(h)+12;
                                h= String.valueOf(h2);
                            }


                            cal.set(Calendar.MONTH, Integer.parseInt(M)-1);
                            cal.set(Calendar.YEAR,  Integer.parseInt(Y));
                            cal.set(Calendar.DAY_OF_MONTH,  Integer.parseInt(D));

                            cal.set(Calendar.HOUR_OF_DAY,  Integer.parseInt(h)-time_offset);
                            cal.set(Calendar.MINUTE,  Integer.parseInt(m));
                            cal.set(Calendar.SECOND, 0);


                            if (cal.compareTo(current) <= 0) {
                                //The set Date/Time already passed
                                Toast.makeText(context,"Invalid Date/Time. Failed to set Notification !",Toast.LENGTH_LONG).show();
                                Log.d("***ALARM:", "Invalid Date/Time" + cal.getTime() + current.getTime());
                            } else {
                                db.toogleAlarm(toDo);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm a");
                                String current_datetime = simpleDateFormat.format(new Date());
                                String target_datetime = simpleDateFormat.format(cal.getTime());
                                Toast.makeText(context,  "Alarm is set at " + target_datetime, Toast.LENGTH_SHORT).show();
                                Log.d( "***CURRENT:" , current_datetime);
                                Log.d( "***TARGET:" , target_datetime);
                                Intent intent = new Intent(context, AlarmReceiver.class);
                                intent.putExtra("ID",id);
                                intent.putExtra("TITLE",toDo.getTitle());
                                intent.putExtra("DETAILS",toDo.getDetails());
                                Log.d("***IDMAX:", String.valueOf(id));
                                Log.d("***TITLE:", toDo.getTitle());
                                Log.d("***DETAILS:", toDo.getDetails());
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                long alarmTime= cal.getTimeInMillis();

                                alarmManager.setExact(AlarmManager. RTC_WAKEUP, alarmTime, pendingIntent);
                                Toast.makeText(context, "ToDo Updated. Notification set .", Toast.LENGTH_LONG).show();
                            }
                        }
                        Intent i=new Intent(context,MainActivity.class);
                        context.startActivity(i);

                        Log.d("ID:","lISTVIEW:"+position+"  TABLE_ID:"+ID_TABLE);
                    }
                });
                alert.setNegativeButton("Cancel",null);
                alert.show();
            }
        });

        holder.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.setTitle("Delete ToDo !!!");
                alert.setMessage("Are you sure?");
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteToDo(toDo);
                        cancelAlarm(toDo.getId());
                        Intent i=new Intent(context,MainActivity.class);
                        context.startActivity(i);

                        Log.d("ID:","lISTVIEW:"+position+"  TABLE_ID:"+ID_TABLE);
                    }
                });
                alert.setNegativeButton("Cancel",null);
                alert.show();


            }
        });

        holder.editIteamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewToDo.class);
                intent.putExtra("ID_TABLE", ID_TABLE);
                intent.putExtra("TITLE",toDo.getTitle());
                intent.putExtra("DETAILS",toDo.getDetails());
                intent.putExtra("DATE",toDo.getDate());
                intent.putExtra("TIME",toDo.getTime());
                intent.putExtra("NOTIFICATION_STATE",toDo.getNotification_state());
                context.startActivity(intent);



            }
        });


    }
    private void cancelAlarm(int id)
    {
        Intent intent = new Intent(this.context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context,id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context," Alarm Cancelled ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {

        return toDoList.size();
    }
}
