package de.stuckdexter.plant.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.stuckdexter.plant.R;

public class PlantFragment extends Fragment {

    private Integer humindity = 0;

    private ImageView drip0 = null;
    private ImageView drip1 = null;
    private ImageView drip2 = null;
    private ImageView drip3 = null;
    private ImageView drip4 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ui = inflater.inflate(R.layout.plant_fragment, container, false);
        return ui;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean startup = false;

        if( getArguments() != null) {
            humindity = this.getArguments().getInt("humindity");
            startup = this.getArguments().getBoolean("startup", false);
        }else{
            humindity = 0;
        }

        View view = getView();
        if(view != null) {
        }
        if(!startup){
            updateUI(humindity);
        }
    }

    public void updateUI(Integer humindity){
        View view = getView();
        if(view != null) {
            ProgressBar bar = view.findViewById(R.id.health_progress);
            TextView textView = view.findViewById(R.id.health);

            Log.d("Fragment", "Updated UI");

            Integer pos = 0;

            //Max: 840
            //Min: 420

            //Dry: 735

            //Middle: 630

            //Water: 525

            //more Water: 472

            int i = humindity - 420;
            pos = 100 - ((i * 100) / 420);

            ObjectAnimator.ofInt(bar, "progress", pos)
                    .setDuration(pos * 10)
                    .start();

            if (humindity > 735) {
                textView.setText(getString(R.string.too_dry));
            }
            if (humindity < 735 && humindity > 630) {
                textView.setText(getString(R.string.too_dry));
            }
            if (humindity < 630 && humindity > 525) {
                textView.setText(getString(R.string.perfect));
            }
            if (humindity < 535 && humindity > 475) {
                textView.setText(getString(R.string.too_wet));
            }
            if (humindity < 475 && humindity > 0) {
                textView.setText(getString(R.string.too_wet));
            }
        }
    }
}