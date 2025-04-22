package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.ActivityMainBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;

public class MainActivity extends AppCompatActivity implements AbstractView {


    private final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private CrosswordMagicController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* Create Controller and Model */

        controller = new CrosswordMagicController();

        Integer puzzleid = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            puzzleid = extras.getInt("puzzleid");
        }
        CrosswordMagicModel model = new CrosswordMagicModel(this, puzzleid);

        /* Register View(s) and Model(s) with Controller */

        controller.addModel(model);
        controller.addView(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (controller.getPuzzle() != null) {
            controller.getPuzzle().saveState(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller.getPuzzle() != null) {
            controller.getPuzzle().loadState(this);
        }
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {
    }

    public CrosswordMagicController getController() {
        return controller;
    }
}