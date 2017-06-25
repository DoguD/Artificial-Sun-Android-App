package layout;

//Gerekli eklentiler import ediliyor

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.oriens.bluelight.R;
import co.oriens.bluelight.presenter.AlarmsListActivity;


public class SetAlarmFragment extends Fragment {
    //Declare Layout Elements
    Button buttonStartAlarmActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set the layout of the fragment
        View layout = inflater.inflate(R.layout.fragment_set_alarm, container, false);
        //Set Layout Elements
        buttonStartAlarmActivity = (Button) layout.findViewById(R.id.buttonStartAlarmActivity);
        buttonStartAlarmActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartAlarmActivity();
            }
        });

        return layout;
    }

    //Method for starting alarm activity
    void StartAlarmActivity(){
        startActivity(new Intent(getActivity(),AlarmsListActivity.class));
    }
}
