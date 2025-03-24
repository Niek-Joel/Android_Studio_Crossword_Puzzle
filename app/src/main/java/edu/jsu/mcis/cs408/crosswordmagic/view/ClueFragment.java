package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.FragmentClueBinding;

public class ClueFragment extends Fragment implements AbstractView {
    public static final String TAB_TITLE = "Clues";
    public static final String ARG_ID = "clueID";
    private CrosswordMagicController controller;
    private FragmentClueBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* get controller, register Fragment as a View */
        this.controller = ((MainActivity)getContext()).getController();
        controller.addView(this);
        fillClues();
    }

    public void fillClues() {
        // Get clues from controller (controller gets clues from model)
        String cluesAcross = controller.getCluesAcross(this);
        String cluesDown = controller.getCluesDown(this);

        // Fill clues into textViews
        binding.aContainer.setText(cluesAcross);
        binding.dContainer.setText(cluesDown);
    }

}
