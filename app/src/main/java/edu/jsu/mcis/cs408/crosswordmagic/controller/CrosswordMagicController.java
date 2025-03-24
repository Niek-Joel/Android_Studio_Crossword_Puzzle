package edu.jsu.mcis.cs408.crosswordmagic.controller;

import static java.security.AccessController.getContext;

import android.view.View;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.view.ClueFragment;

public class CrosswordMagicController extends AbstractController {

    public static final String TEST_PROPERTY = "TestProperty";
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimensions";
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

    public String getCluesAcross(ClueFragment fragment) {
        daoFactory = new DAOFactory(fragment.getContext());
        Puzzle puzzle = daoFactory.getPuzzleDAO().find(1);
        return puzzle.getCluesAcross();
    }

    public String getCluesDown(ClueFragment fragment) {
        daoFactory = new DAOFactory(fragment.getContext());
        Puzzle puzzle = daoFactory.getPuzzleDAO().find(1);
        return puzzle.getCluesDown();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
    }
}