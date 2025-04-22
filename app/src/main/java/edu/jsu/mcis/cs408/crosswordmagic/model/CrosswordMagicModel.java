package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;
import edu.jsu.mcis.cs408.crosswordmagic.view.CrosswordGridView;

public class CrosswordMagicModel extends AbstractModel {

    private final int DEFAULT_PUZZLE_ID = 1;

    private Puzzle puzzle;
    private CrosswordGridView gridView;
    private CrosswordMagicController controller;
    private DAOFactory daoFactory;
    private final String TAG = "CroswwordMagicModel";

    public CrosswordMagicModel(Context context) {

        DAOFactory daoFactoryInternal = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactoryInternal.getPuzzleDAO();

        this.puzzle = puzzleDAO.find(DEFAULT_PUZZLE_ID);
        this.daoFactory = daoFactoryInternal;
    }
    public CrosswordMagicModel(Context context, Integer puzzleid) {

        DAOFactory daoFactoryInternal = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactoryInternal.getPuzzleDAO();

        this.puzzle = puzzleDAO.find(puzzleid);
        this.daoFactory = daoFactoryInternal;
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

    public void getPuzzleList() {
        SQLiteDatabase db = daoFactory.getPuzzleDAO().getDb();
        PuzzleListItem[] puzzles = daoFactory.getPuzzleDAO().listPuzzles(db);
        firePropertyChange(CrosswordMagicController.PUZZLE_LIST_PROPERTY, null, puzzles);
    }

    public void getPuzzleMenu() {
        // Get JSON Array
        JSONArray jsonList  = daoFactory.getWebServiceDAO().list();

        // Convert to list of PuzzleItems
        ArrayList<PuzzleListItem> Arraylist = new ArrayList<>();
        PuzzleListItem[] list = null;
        try {
            for (int i=0; i<jsonList.length(); i++) {
                JSONObject obj = jsonList.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                Arraylist.add(new PuzzleListItem(id, name));
            }
            list = Arraylist.toArray(new PuzzleListItem[0]);

            // Send to controller as array
            firePropertyChange(CrosswordMagicController.PUZZLE_MENU_PROPERTY, null, list);
        }
        catch (Exception e) {
            Log.e(TAG, "JSONArray to PuzzeListItem[] failed");
        }
    }

    // TODO: Maybe not necessary. Properly grab puzzle meta data
    public void getWebPuzzleMetaData() {

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

    public void setDownloadPuzzle(Integer webID) {
        JSONObject jsonObject = daoFactory.getWebServiceDAO().getSelectedPuzzle(webID);
        String name = null;

        if (jsonObject != null) {
            try {
                /* get meta data for puzzle */
                // TODO: Figure out how to grab Puzzle metaData. Currently the JSONObject puzzle returns one layer too deep.
                //  I.e. clues,words,positions instead of name,description,width,etc.
//                JSONObject puzzle = daoFactory.getWebServiceDAO().getSelectedPuzzle(webID);
//                name = jsonObject.getString(daoFactory.getProperty("name"));
//                String description = jsonObject.getString("description");
//                String height = jsonObject.getString("height");
//                String width = jsonObject.getString("width");

                HashMap<String, String> params = new HashMap<>();
                name = "Online Puzzle #" + webID;
                params.put(daoFactory.getProperty("sql_field_name"), name);
                params.put(daoFactory.getProperty("sql_field_description"), "DISCRIPTION PLACEHOLDER");
                params.put(daoFactory.getProperty("sql_field_height"), "15");
                params.put(daoFactory.getProperty("sql_field_width"), "15");

                Puzzle puzzle = new Puzzle(params);
                int puzzleId = daoFactory.getPuzzleDAO().create(puzzle);
                // set puzzle id?

                // Insert words into puzzle object
                JSONArray jsonArrayWords = jsonObject.getJSONArray("puzzle");
                ArrayList<Word> words = new ArrayList<>();

                for (int i=0; i<jsonArrayWords.length(); i++) {
                    JSONObject jsonObjectWord = jsonArrayWords.getJSONObject(i);
                    HashMap<String,String> wordParams = new HashMap<>();

                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_puzzleid")), String.valueOf(puzzleId));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_row")), String.valueOf(jsonObjectWord.getInt("row")));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_column")), String.valueOf(jsonObjectWord.getInt("column")));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_box")), String.valueOf(jsonObjectWord.getInt("box")));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_direction")), String.valueOf(jsonObjectWord.getInt("direction")));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_word")), jsonObjectWord.getString("word"));
                    wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_clue")), jsonObjectWord.getString("clue"));

                    Word word = new Word(wordParams);
                    daoFactory.getWordDAO().create(word);
                    words.add(word);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        firePropertyChange(CrosswordMagicController.DOWNLOAD_PUZZLE_PROPERTY, null,name);
    }

    public void loadState(Context context) {
        if (puzzle != null) {
            puzzle.loadState(context);
        }
    }

    public void saveState(Context context) {
        if (puzzle != null) {
            puzzle.saveState(context);
        }
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }
}