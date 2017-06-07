package galactus.ron.arckin;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Map;

public class CalenderActivity extends AppCompatActivity {

    EditText EventEditText;
    ImageButton closeCalendarButton;
    ListView listView;
    int hour,min;
    private String selectedDate,dateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        // Connect to the Firebase database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Bundle bundle = getIntent().getExtras();
        final String userNameRef = bundle.getString("UserName");
        final DatabaseReference calenderRef= database.getReference("Users/"+ userNameRef+"/Calendar");
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.calendarListView);

        // Create a new Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        assert listView != null;
        listView.setAdapter(adapter);

        // Get the application context
        Context mContext = getApplicationContext();

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        final View customView = inflater.inflate(R.layout.popupcalendar,null);
        // Initialize a new instance of popup window
        final PopupWindow mPopupWindow = new PopupWindow(
                customView,
                480 ,
                750,true
        );
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();

        final NumberPicker np2 = (NumberPicker) customView.findViewById(R.id.numberPicker2);
        final NumberPicker np3 = (NumberPicker) customView.findViewById(R.id.numberPicker3);
        closeCalendarButton=(ImageButton) customView.findViewById(R.id.ib_close);
        EventEditText= (EditText) customView.findViewById(R.id.eventEditText);
        final Button CalendarSubmitButton= (Button) customView.findViewById(R.id.calendarSubmitButton);

        //Set the minimum value of NumberPicker
        assert np2 != null;
        np2.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np2.setMaxValue(23);

        //Set the minimum value of NumberPicker
        assert np3 != null;
        np3.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np3.setMaxValue(59);



        // Finally, show the popup window at the center location of root relative layout
        final RelativeLayout mRelativeLayout= (RelativeLayout) findViewById(R.id.activity_calender);

        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                //   moods[0] = Integer.toString(newVal);
                hour=newVal;

            }
        });

        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                //   moods[0] = Integer.toString(newVal);
                min=newVal;

            }
        });

        final  CalendarView simpleCalendarView = (CalendarView) findViewById(R.id.calendarView); // get the reference of CalendarView


        assert simpleCalendarView != null;
        simpleCalendarView.setFirstDayOfWeek(2); // set Monday as the first day of the week


        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = ( dayOfMonth +" - " + (month+1) + " - " + year);
                adapter.clear();
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,40); calenderRef.child(selectedDate).addChildEventListener(new ChildEventListener(){

                    // This function is called once for each child that exists
                    // when the listener is added. Then it is called
                    // each time a new child is added.
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                       /* Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        adapter.add(map.toString());*/

                       String value= "TITLE: "+dataSnapshot.getKey()+"\nTIME: "+dataSnapshot.getValue();
                        adapter.add(value);
                    }

                    // This function is called each time a child item is removed.
                    public void onChildRemoved(DataSnapshot dataSnapshot){
                        String value = dataSnapshot.getValue(String.class);
                        adapter.remove(value);
                    }

                    // The following functions are also required in ChildEventListener implementations.
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName){}
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName){}

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("TAG:", "Failed to read value.", error.toException());
                    }
                });

        }


        });
        CalendarSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime= hour+":"+min;
                Toast.makeText(getApplicationContext(),"Event Added",Toast.LENGTH_SHORT).show();
                calenderRef.child(selectedDate).child(EventEditText.getText().toString()).setValue(dateTime);
                mPopupWindow.dismiss();
                Toast.makeText(CalenderActivity.this,"Event Added!",Toast.LENGTH_SHORT).show();
            }
        });
        // Set a click listener for the popup window close button
        closeCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();

            }
        });


    }
}
