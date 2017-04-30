package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.io.IOException;

import galactus.ron.arckin.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment {


    public DeviceFragment() {
        // Required empty public constructor
    }
    EditText deviceEditText;
    Button deviceButton;
    // Connect to the Firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference root=database.getReference("Devices");
    String filename="DeviceID.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        deviceEditText=(EditText)view.findViewById(R.id.deviceIDEditText);
        deviceButton=(Button)view.findViewById(R.id.deviceIDButton);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(deviceEditText.getText().toString()))
                        {  Toast.makeText(getContext(),"Device Exist, Changing Device",Toast.LENGTH_SHORT).show();

                            try {
                                FileOutputStream fileOutputStream=getActivity().openFileOutput(filename,MODE_PRIVATE);
                                fileOutputStream.write(deviceEditText.getText().toString().getBytes());
                                fileOutputStream.close();
                                Toast.makeText(getContext(),"saved to text file",Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{Toast.makeText(getContext(),"no Such Device",Toast.LENGTH_SHORT).show();}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
        return view;
    }

}
