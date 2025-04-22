package edu.jsu.mcis.cs408.crosswordmagic.controller;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Pair;
import android.view.View;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.model.AbstractModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.view.AbstractView;
import edu.jsu.mcis.cs408.crosswordmagic.view.ClueFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.CrosswordGridView;
import edu.jsu.mcis.cs408.crosswordmagic.view.MainActivity;
import edu.jsu.mcis.cs408.crosswordmagic.view.MenuActivity;
import edu.jsu.mcis.cs408.crosswordmagic.view.PuzzleFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.WelcomeActivity;

public class CrosswordMagicController extends AbstractController {

    public static final String TEST_PROPERTY = "TestProperty";
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimensions";
    public static final String CLUES_ACROSS_PROPERTY = "CluesAcross";
    public static final String CLUES_DOWN_PROPERTY = "CluesDown";
    public static final String GUESS_PROPERTY = "Guess";
    public static final String PUZZLE_LIST_PROPERTY = "PuzzleList";
    public static final String PUZZLE_MENU_PROPERTY = "PuzzleMenu";
    public static final String DOWNLOAD_PUZZLE_PROPERTY = "DownloadPuzzle";
    private Puzzle puzzle;


    public void tryGuess(String input, Integer boxNum){
        Pair<Integer, String> pair = new Pair<> (boxNum, input);
        setModelProperty(GUESS_PROPERTY, pair);
    }

    public void downloadPuzzle(Integer id) {
        setModelProperty(DOWNLOAD_PUZZLE_PROPERTY, id);
    }

    public void getGridDimensions() { getModelProperty(GRID_DIMENSION_PROPERTY);}
    public void getGridLetters() { getModelProperty(GRID_LETTERS_PROPERTY);}
    public void getGridNumbers() { getModelProperty(GRID_NUMBERS_PROPERTY);}
    public void getCluesAcross() { getModelProperty(CLUES_ACROSS_PROPERTY);}
    public void getCluesDown() { getModelProperty(CLUES_DOWN_PROPERTY);}
    public void getPuzzleList() { getModelProperty(PUZZLE_LIST_PROPERTY);}

    public void getPuzzleMenu() {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                ((CrosswordMagicModel) model).getPuzzleMenu();
            }
        }
    }

    public Puzzle getPuzzle() {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                return ((CrosswordMagicModel) model).getPuzzle();
            }
        }
        return null;
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
            case GUESS_PROPERTY:
                for (AbstractView view: views) {
                    if (view instanceof PuzzleFragment) {
                        ((PuzzleFragment) view).modelPropertyChange(evt);
                    }
                    else if (view instanceof CrosswordGridView) {
                        ((CrosswordGridView) view).modelPropertyChange(evt);
                    }
                }
                break;
            case PUZZLE_MENU_PROPERTY:
                for (AbstractView view: views) {
                    if (view instanceof MenuActivity) {
                        ((MenuActivity) view).modelPropertyChange(evt);
                    }
                }
                break;
            case DOWNLOAD_PUZZLE_PROPERTY:  // Update spinner
                for (AbstractView view: views) {
                    if (view instanceof WelcomeActivity) {
                        ((WelcomeActivity) view).modelPropertyChange(evt);
                    }
                }
                break;
        }
    }

    public void loadState(Context context) {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                ((CrosswordMagicModel) model).loadState(context);
            }
        }
    }

    public void saveState(Context context) {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                ((CrosswordMagicModel) model).saveState(context);
            }
        }
    }

}