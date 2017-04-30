package galactus.ron.arckin;

import android.app.AlarmManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Alarm extends AppCompatActivity {
    TimePicker alarmTimePicker;
    AlarmManager alarmManager;
    Switch alarmSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);



        // Connect to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        Bundle bundle = getIntent().getExtras();
        String userNameRef = bundle.getString("UserName");
        // Get a reference to the todoItems child items it the database
        final DatabaseReference myRef = database.getReference("Users/"+userNameRef+ "/Alarm");

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmSwitch= (Switch)findViewById(R.id.alarmSwitch);


        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(Alarm.this, "ALARM ON", Toast.LENGTH_SHORT).show();
                    int hour = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hour = alarmTimePicker.getHour();
                    }
                    int minute = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        minute = alarmTimePicker.getMinute();
                    }
                    String time = String.valueOf(hour) + ":" + String.valueOf(minute);
                    myRef.child("AlarmTime").setValue(time);
                    myRef.child("Status").setValue(1);
                }else{

                    myRef.child("AlarmTime").setValue("");
                    myRef.child("Status").setValue(0);
                }

            }
        });

    }




}
