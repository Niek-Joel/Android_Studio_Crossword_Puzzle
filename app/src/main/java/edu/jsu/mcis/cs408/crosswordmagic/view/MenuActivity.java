package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.ActivityMenuBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class MenuActivity extends AppCompatActivity implements AbstractView{
    private ActivityMenuBinding binding;
    private final PuzzleItemClickHandeler itemClick = new PuzzleItemClickHandeler();
    private CrosswordMagicController controller;
    private Integer highlightedPuzzle = null;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

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

        recyclerView = binding.recyclerOutput;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        controller.getPuzzleMenu();

        // Shift intent to Main Activity
        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download puzzle
                controller.downloadPuzzle(highlightedPuzzle);

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
        switch (evt.getPropertyName()) {
            case CrosswordMagicController.PUZZLE_MENU_PROPERTY:
                PuzzleListItem[] menuItems = (PuzzleListItem[])evt.getNewValue();
                ArrayList<PuzzleListItem> list = new ArrayList<>(Arrays.asList(menuItems));
                adapter = new RecyclerViewAdapter(list, this);
                recyclerView.setAdapter(adapter);
            break;
            case CrosswordMagicController.DOWNLOAD_ERROR_DUPLICATE:
                Toast.makeText(this.getBaseContext(), "Puzzle Already Downloaded \nRedirected To Default Puzzle", Toast.LENGTH_SHORT).show();
                break;
        }
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