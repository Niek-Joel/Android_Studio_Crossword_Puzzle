package edu.jsu.mcis.cs408.crosswordmagic.controller;

import static java.security.AccessController.getContext;

import android.view.View;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.view.AbstractView;
import edu.jsu.mcis.cs408.crosswordmagic.view.ClueFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.PuzzleFragment;

public class CrosswordMagicController extends AbstractController {

    public static final String TEST_PROPERTY = "TestProperty";
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimensions";
    public static final String CLUES_ACROSS_PROPERTY = "CluesAcross";
    public static final String CLUES_DOWN_PROPERTY = "CluesDown";
    DAOFactory daoFactory;
    public void getTestProperty(String value) {
        getModelProperty(TEST_PROPERTY);
    }
    public void getGridDimensions() {
        getModelProperty(GRID_DIMENSION_PROPERTY);
    }
    public void getGridLetters() {
        getModelProperty(GRID_LETTERS_PROPERTY);
    }
    public void getGridNumbers() {
        getModelProperty(GRID_NUMBERS_PROPERTY);
    }

    public void getCluesAcross() {
        getModelProperty(CLUES_ACROSS_PROPERTY);
    }

    public void getCluesDown() {
        getModelProperty(CLUES_DOWN_PROPERTY);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        switch (evt.getPropertyName()) {
            case CLUES_ACROSS_PROPERTY:
                for (AbstractView view: views) {
                    if (view instanceof ClueFragment) {
                        ((ClueFragment) view).setCluesAcross(evt.getNewValue().toString());
                    }
                }
                break;
            case CLUES_DOWN_PROPERTY:
                for (AbstractView view: views) {
                    if (view instanceof ClueFragment) {
                        ((ClueFragment) view).setCluesDown(evt.getNewValue().toString());
                    }
                }
                break;
        }
    }
}