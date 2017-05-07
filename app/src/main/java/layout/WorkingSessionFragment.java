package layout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.oriens.bluelight.FullScreenWakeUp;
import co.oriens.bluelight.FullScreenWork;
import co.oriens.bluelight.R;

public class WorkingSessionFragment extends Fragment {

    //Declare Objects
    //Start session button
    Button startWorkingSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_working_session, container, false);

        //Define objects
        //Start session button
        startWorkingSession =(Button) layout.findViewById(R.id.startWorkingButton);
        startWorkingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Full Screen Activity
                WorkingSessionFragment.this.startActivity(new Intent(getActivity(), FullScreenWork.class));
            }
        });

        return layout;
    }
}
