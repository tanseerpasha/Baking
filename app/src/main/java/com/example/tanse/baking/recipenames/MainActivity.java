package com.example.tanse.baking.recipenames;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tanse.baking.R;
import com.example.tanse.baking.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SyncAdapter.initializeSyncAdapter(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_names, new NameFragment())
                    .commit();
        }
    }

}
