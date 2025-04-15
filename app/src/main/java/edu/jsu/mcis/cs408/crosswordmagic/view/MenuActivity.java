package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.ActivityMenuBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class MenuActivity extends AppCompatActivity implements AbstractView{
    private ActivityMenuBinding binding;
    private final PuzzleItemClickHandeler itemClick = new PuzzleItemClickHandeler();
    private CrosswordMagicController controller;
    private Integer highlightedPuzzle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        controller = new CrosswordMagicController();
        CrosswordMagicModel model = new CrosswordMagicModel(this);

        controller.addModel(model);
        controller.addView(this);

        // Request Puzzle list From Server
        // TODO

        // Shift intent to Main Activity
        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download puzzle


                // Switch activities with new puzzle
                Intent i = new Intent(MenuActivity.this, MainActivity.class);
                // If no puzzle has been selected
                Integer puzzleid = highlightedPuzzle;
                if (puzzleid == null) {
                    puzzleid = 1;
                }
                i.putExtra("puzzleid", puzzleid);
                startActivity(i);
            }
        });
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {

    }

    private class PuzzleItemClickHandeler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = binding.recyclerOutput.getChildAdapterPosition(v);
            RecyclerViewAdapter adapter = (RecyclerViewAdapter)binding.recyclerOutput.getAdapter();
            if (adapter != null) {
                PuzzleListItem puzzleListItem = adapter.getItem(position);
                highlightedPuzzle = puzzleListItem.getId();
                Toast.makeText(v.getContext(), String.valueOf(highlightedPuzzle), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public PuzzleItemClickHandeler getItemClick() {
        return itemClick;
    }
}