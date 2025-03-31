package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;
import edu.jsu.mcis.cs408.crosswordmagic.view.CrosswordGridView;

public class CrosswordMagicModel extends AbstractModel {

    private final int DEFAULT_PUZZLE_ID = 1;

    private Puzzle puzzle;
    private CrosswordGridView gridView;
    private CrosswordMagicController controller;

    public CrosswordMagicModel(Context context) {

        DAOFactory daoFactory = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();

        this.puzzle = puzzleDAO.find(DEFAULT_PUZZLE_ID);
    }
    public void getTestProperty() {

        String wordCount = (this.puzzle != null ? String.valueOf(puzzle.getSize()) : "none");
        firePropertyChange(CrosswordMagicController.TEST_PROPERTY, null, wordCount);

    }

    public void getCluesAcross() {
        String cluesAcross = puzzle.getCluesAcross();
        firePropertyChange(CrosswordMagicController.CLUES_ACROSS_PROPERTY, null, cluesAcross);
    }
    public void getCluesDown() {
        String cluesDown = puzzle.getCluesDown();
        firePropertyChange(CrosswordMagicController.CLUES_DOWN_PROPERTY, null, cluesDown);
    }

    public void getGridDimensions() {
        Integer height = puzzle.getHeight();
        Integer width = puzzle.getWidth();
        Integer[] dimension = {height, width};
        firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, dimension);
    }

    public void getGridLetters() {
        Character[][] letters = puzzle.getLetters();
        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, letters);
    }

    public void getGridNumbers() {
        Integer[][] numbers = puzzle.getNumbers();
        firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, numbers);
    }

    public void setGuess(Pair<Integer, String> pair) {
        Integer boxNum = pair.first;
        String guess = pair.second;
        WordDirection direction = puzzle.checkGuess(boxNum, guess);
        if (direction != null) { // If Null then Incorrect Guess
            firePropertyChange(CrosswordMagicController.GUESS_PROPERTY, null, direction);
        }
        else {
            firePropertyChange(CrosswordMagicController.GUESS_PROPERTY, null, null);
        }
    }
}