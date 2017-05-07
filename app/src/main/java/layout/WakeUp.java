package layout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import co.oriens.bluelight.FullScreenWakeUp;
import co.oriens.bluelight.R;
import co.oriens.bluelight.TabActivity;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class WakeUp extends Fragment {

    //OBJECTS
    //start button
    Button startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_wake_up, container, false);

        //STARTING THE FULL SCREEN ACTIVITY
        //Define start button & OnClick Listener
        startButton = (Button) layout.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartButtonPressed();
            }
        });

        //Return Layout
        return layout;

    }

    //Start Button Pressed Method
    public void StartButtonPressed(){
        //Start Full Screen Activity
        WakeUp.this.startActivity(new Intent(getActivity(), FullScreenWakeUp.class));
    }


}
