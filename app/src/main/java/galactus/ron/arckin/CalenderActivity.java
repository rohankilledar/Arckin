package galactus.ron.arckin;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalenderActivity extends AppCompatActivity {


    EditText eventET;
    Button eventB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        final  CalendarView simpleCalendarView = (CalendarView) findViewById(R.id.calendarView); // get the reference of CalendarView


        // Connect to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Bundle bundle = getIntent().getExtras();
        String userNameRef = bundle.getString("UserName");
        final DatabaseReference calenderRef= database.getReference("Users/"+ userNameRef+"/Calendar");


        simpleCalendarView.setFirstDayOfWeek(2); // set Monday as the first day of the week



        eventB= (Button) findViewById(R.id.eventButton);
        eventET= (EditText) findViewById(R.id.eventEditText);
        final String[] selectedDate = new String[1];
        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
               selectedDate[0] = ( dayOfMonth +" - " + (month+1) + " - " + year);
            }
        });
        eventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                long selectedDate = simpleCalendarView.getDate(); // get selected date in milliseconds
                SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
                String dateString= formatter.format(new Date(selectedDate));*/
                DatabaseReference childRef = calenderRef.push();
                childRef.child(selectedDate[0]).setValue(eventET.getText().toString());
                Toast.makeText(getApplicationContext(),"Event Added",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
