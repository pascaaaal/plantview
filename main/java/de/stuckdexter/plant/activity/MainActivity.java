package de.stuckdexter.plant.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import de.stuckdexter.plant.R;
import de.stuckdexter.plant.fragments.PlantFragment;

import static java.lang.Math.toIntExact;

public class MainActivity extends AppCompatActivity {


    private Fragment aktivFragment = null;
    private int index = 0;

    private static Long galerie = 0L;
    private static Long wohnzimmer = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Long>> hashMap = (HashMap<String, HashMap<String, Long>>) dataSnapshot.getValue();

                HashMap<String, Long> galerie = hashMap.get("Galerie");
                HashMap<String, Long> wohnzimmer = hashMap.get("Wohnzimmer");

                MainActivity.galerie = galerie.get("humindity");
                MainActivity.wohnzimmer = wohnzimmer.get("humindity");
                Log.d("Database", MainActivity.galerie + ":" + MainActivity.wohnzimmer);
                if(index == 0) {
                    ((PlantFragment) aktivFragment).updateUI(toIntExact(MainActivity.galerie));
                }else{
                    ((PlantFragment) aktivFragment).updateUI(toIntExact(MainActivity.wohnzimmer));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        setTitle("Galerie");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("de.stuckdexter.plant.notification.plant_too_dry", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        setContentView(R.layout.activity_main);

        final TextView name = findViewById(R.id.name);

        final ImageView right = findViewById(R.id.right);
        final ImageView left = findViewById(R.id.left);

        left.setVisibility(View.GONE);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == 0) {
                    index++;
                    setPlant(wohnzimmer, false);
                    name.setText("Wohnzimmer");
                    right.setVisibility(View.GONE);
                    left.setVisibility(View.VISIBLE);
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                setPlant(galerie, false);
                name.setText("Galerie");
                left.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);
            }
        });

        setPlant(galerie, true);
    }

    private void setPlant(long humindity, boolean startup){
        Bundle d = new Bundle();
        d.putInt("humindity", toIntExact(humindity));
        d.putBoolean("startup", startup);

        Fragment fr = new PlantFragment();
        fr.setArguments(d);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fr);
        fragmentTransaction.commit();
        aktivFragment = fr;
    }
}