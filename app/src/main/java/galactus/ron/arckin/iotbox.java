package galactus.ron.arckin;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ramotion.foldingcell.FoldingCell;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class iotbox extends AppCompatActivity {
    String filename="DeviceID.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iotbox);
        final SeekBar rSeekBar, gSeekBar, bSeekBar;
        //  final String[] moods = new String[1];
        final int[] mood = new int[1];
        final ImageView colorShow;
        colorShow = (ImageView) findViewById(R.id.imageView4);
        rSeekBar = (SeekBar) findViewById(R.id.redSeekBar);
        gSeekBar = (SeekBar) findViewById(R.id.greenSeekBar);
        bSeekBar = (SeekBar) findViewById(R.id.blueSeekBar);

        String inputText;
        String DeviceIdRef = null;
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputText = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputText);
            }
            DeviceIdRef = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Connect to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        // Get a reference to the todoItems child items it the database

        final DatabaseReference myRef = database.getReference("IoTBox/" + DeviceIdRef + "/LCDDisplay");
        final DatabaseReference servoRef = database.getReference("IoTBox/" + DeviceIdRef + "/Curtains");
        final DatabaseReference ledRef = database.getReference("IoTBox/" + DeviceIdRef + "/LEDLights");
        final ToggleButton servoToggle = (ToggleButton) findViewById(R.id.foldingCellServoToggle);
        final ToggleButton lightToggle = (ToggleButton) findViewById(R.id.LEDtoggleButton);
        final EditText text = (EditText) findViewById(R.id.ledText);
        final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);


// get our folding cell
        final FoldingCell foldingCell = (FoldingCell) findViewById(R.id.folding_cell);
        final FoldingCell ledFoldingCell = (FoldingCell) findViewById(R.id.folding_cellLED);
        final FoldingCell lightsFoldingCell = (FoldingCell) findViewById(R.id.folding_cellLights);
        // set custom parameters(anim duration,color,flip count)
        assert foldingCell != null;
        foldingCell.initialize(1000, Color.rgb(41, 121, 255), 3);
        assert ledFoldingCell != null;
        ledFoldingCell.initialize(1000, Color.rgb(41, 121, 255), 5);
        assert lightsFoldingCell != null;
        lightsFoldingCell.initialize(1000, Color.rgb(41, 121, 255), 5);
// or with camera height parameter
        // foldingCell.initialize(30, 1000, Color.DKGRAY, 2);
        // attach click listener to folding cell
        foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldingCell.toggle(false);
            }
        });
        ledFoldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledFoldingCell.toggle(false);
            }
        });

        lightsFoldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightsFoldingCell.toggle(false);
            }
        });

        //Initializing a new string array with elements
        final String[] values = {"mood1", "mood2", "mood3", "mood4"};
        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        assert np != null;
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(3);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);
        np.setDisplayedValues(values);
        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                //   moods[0] = Integer.toString(newVal);
                mood[0] = newVal;

            }
        });


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rseekBarValue = 0;
                if (rSeekBar != null) {
                    rseekBarValue = rSeekBar.getProgress();
                }
                assert gSeekBar != null;
                int gseekBarValue = gSeekBar.getProgress();
                assert bSeekBar != null;
                int bseekBarValue = bSeekBar.getProgress();
                String hexColor = String.format("#%02x%02x%02x", rseekBarValue, gseekBarValue, bseekBarValue);
                assert colorShow != null;
                colorShow.setBackgroundColor(Color.parseColor(hexColor));

                Snackbar.make(view, "Displaying on LED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                // Create a new child with a auto-generated ID.
                //   DatabaseReference childRef = myRef.push();
                assert text != null;
                myRef.child("text").setValue(text.getText().toString());
                myRef.child("Color").child("Red").setValue(rseekBarValue);
                myRef.child("Color").child("Green").setValue(gseekBarValue);
                myRef.child("Color").child("Blue").setValue(bseekBarValue);
                // Set the child's data to the value passed in from the text box.
                //childRef.setValue(text.getText().toString());

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fab.getWindowToken(), 0);
            }
        });

   /*    servoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> map = (Map<String, Integer>) dataSnapshot.getValue();
                int status=map.get("Switch");
                if (status==1)
                servoToggle.setChecked(true);
                else
                    servoToggle.setChecked(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/

        assert servoToggle != null;
        servoToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    servoRef.child("Switch").setValue(1);
                    Snackbar.make(buttonView, "Curtain opening", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    servoRef.child("Switch").setValue(0);
                    Snackbar.make(buttonView, "Curtain closing", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
// perform seek bar change listener event used for getting the progress value
        assert rSeekBar != null;
        rSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                String hexColor = String.format("#%02x%02x%02x", rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());
                colorShow.setBackgroundColor(Color.parseColor(hexColor));
                myRef.child("Color").child("Red").setValue(rSeekBar.getProgress());

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(iotbox.this, "Red Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });

        gSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                String hexColor = String.format("#%02x%02x%02x", rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());
                colorShow.setBackgroundColor(Color.parseColor(hexColor));
                myRef.child("Color").child("Green").setValue(gSeekBar.getProgress());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(iotbox.this, "Green Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });

        bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                String hexColor = String.format("#%02x%02x%02x", rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());
                colorShow.setBackgroundColor(Color.parseColor(hexColor));
                myRef.child("Color").child("Blue").setValue(bSeekBar.getProgress());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(iotbox.this, "Blue Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });


        lightToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ledRef.child("Switch").setValue(1);
                    ledRef.child("moods").setValue(mood[0]);
                    Snackbar.make(buttonView, "LED Lights on", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ledRef.child("moods").setValue(mood[0]);
                } else {
                    ledRef.child("Switch").setValue(0);
                    Snackbar.make(buttonView, "LED Lights off", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}