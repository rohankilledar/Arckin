package galactus.ron.arckin;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalenderActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mRelativeLayout;
    private PopupWindow mPopupWindow;
    EditText eventET;
    Button eventB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = CalenderActivity.this;

        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_calender);


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

        }});
        eventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                long selectedDate = simpleCalendarView.getDate(); // get selected date in milliseconds
                SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
                String dateString= formatter.format(new Date(selectedDate));*/
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popupaddress,null);
                  /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
                mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                // Initialize a new instance of popup window
           /*     mPopupWindow = new PopupWindow(
                        customView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);*/

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21) {
                    mPopupWindow.setElevation(5.0f);
                }

                DatabaseReference childRef = calenderRef.push();
                childRef.child(selectedDate[0]).setValue(eventET.getText().toString());
                Toast.makeText(getApplicationContext(),"Event Added",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
