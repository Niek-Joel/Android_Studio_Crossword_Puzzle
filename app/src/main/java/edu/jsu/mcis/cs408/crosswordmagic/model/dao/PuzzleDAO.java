package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.opencsv.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;
import edu.jsu.mcis.cs408.crosswordmagic.model.WordDirection;

public class PuzzleDAO {

    private final DAOFactory daoFactory;
    private SQLiteDatabase db;

    PuzzleDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /* add a new puzzle entry to the database */

    public int create(Puzzle newPuzzle) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        int result = create(db, newPuzzle);
        db.close();
        return result;

    }

    public int create(SQLiteDatabase db, Puzzle newPuzzle) {

        int key = 0;

        /* use this method if there IS already a SQLiteDatabase open */

        String name = daoFactory.getProperty("sql_field_name");
        String description = daoFactory.getProperty("sql_field_description");
        String height = daoFactory.getProperty("sql_field_height");
        String width = daoFactory.getProperty("sql_field_width");

        ContentValues values = new ContentValues();
        values.put(name, newPuzzle.getName());
        values.put(description, newPuzzle.getDescription());
        values.put(height, newPuzzle.getHeight());
        values.put(width, newPuzzle.getWidth());

        key = (int)db.insert(daoFactory.getProperty("sql_table_puzzles"), null, values);

        this.db = db;

        return key;

    }

    /* return an existing puzzle entry from the database */

    public Puzzle find(int puzzleid) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Puzzle result = find(db, puzzleid);
        db.close();
        return result;

    }

    public Puzzle find(SQLiteDatabase db, int puzzleid) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        Puzzle puzzle = null;

        String query = daoFactory.getProperty("sql_get_puzzle");
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});

        if (cursor.moveToFirst()) {

            cursor.moveToFirst();

            /* get data for puzzle */

            HashMap<String, String> params = new HashMap<>();

            /* get data for new puzzle */
            if (cursor.moveToFirst()) {
                params.put(daoFactory.getProperty("sql_field_id"), cursor.getString(0));
                params.put(daoFactory.getProperty("sql_field_name"), cursor.getString(1));
                params.put(daoFactory.getProperty("sql_field_description"), cursor.getString(2));
                params.put(daoFactory.getProperty("sql_field_height"), cursor.getString(3));
                params.put(daoFactory.getProperty("sql_field_width"), cursor.getString(4));
            }

            if ( !params.isEmpty() )
                puzzle = new Puzzle(params);

            /* get list of words (if any) to add to puzzle */

            WordDAO wordDao = daoFactory.getWordDAO();

            ArrayList<Word> words = wordDao.list(db, puzzleid);

            if ( !words.isEmpty() )
                puzzle.addWordsToPuzzle(words);

            cursor.close();

            /* get already-guessed words (if any) for puzzle */

            query = daoFactory.getProperty("sql_get_guesses");

            int boxColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_box_column_index"));
            int directionColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_direction_column_index"));

            cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});

            if (cursor.moveToFirst()) {

                cursor.moveToFirst();

                do {

                    Integer box = cursor.getInt(boxColumnIndex);
                    WordDirection direction = WordDirection.values()[cursor.getInt(directionColumnIndex)];

                    puzzle.addWordToGuessed(box + direction.toString());

                }
                while ( cursor.moveToNext() );

                cursor.close();

            }

        }

        return puzzle;

    }

    // Retrieves list of puzzles from DB
    public PuzzleListItem[] listPuzzles(SQLiteDatabase db) {
        ArrayList<PuzzleListItem> puzzles = new ArrayList<>();

        // Query db for ID and Name (ordered by name)
        String query = daoFactory.getProperty("sql_get_puzzles");
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                puzzles.add(new PuzzleListItem(id, name));
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        // Convert arrayList to array and return
        return puzzles.toArray(new PuzzleListItem[]{});
    }

    public SQLiteDatabase getDb() {
        if (this.db != null) {
            return this.db;
        }
        else {
            return daoFactory.getWritableDatabase();
        }
    }

}