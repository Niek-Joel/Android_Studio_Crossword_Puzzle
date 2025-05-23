package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;

public class CrosswordGridView extends View implements AbstractView {

    private CrosswordMagicController controller;

    private final char BLOCK = '*';

    private final float TEXT_NUMBER_SCALE = 10f;  // Tablet: 4.75f. New value (10) works better for my phone
    private final float TEXT_LETTER_SCALE = 4f;   // Tablet: 1.5f. New value (4) works better for my phone

    private final Paint gridPaint;
    private final TextPaint gridTextPaint;

    private int viewWidth, viewHeight, gridWidth, gridHeight;
    private int squareWidth, squareHeight, xBegin, yBegin, xEnd, yEnd;

    private Character[][] letters;
    private Integer[][] numbers;

    public CrosswordGridView(Context context, AttributeSet attrs) {

        super(context, attrs);

        gridTextPaint = new TextPaint();
        gridTextPaint.setAntiAlias(true);
        gridTextPaint.setColor(Color.BLACK);

        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(new OnTouchHandler(context));

        this.controller = ((MainActivity)context).getController();

        controller.addView(this);

        controller.getGridDimensions();
        controller.getGridLetters();
        controller.getGridNumbers();

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {

        super.onSizeChanged(xNew, yNew, xOld, yOld);

        this.viewWidth = xNew;
        this.viewHeight = yNew;

    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {

        super.onDraw(canvas);

        if (gridWidth > 0 && gridHeight > 0) {

            int gridSize = Math.min(viewWidth, viewHeight);

            this.squareWidth = (gridSize / gridWidth);
            this.squareHeight = (gridSize / gridHeight);

            this.yBegin = (viewHeight - (squareHeight * gridHeight)) / 2;
            this.xBegin = (viewWidth - (squareWidth * gridWidth)) / 2;

            this.yEnd = yBegin + (squareHeight * gridHeight);
            this.xEnd = xBegin + (squareWidth * gridWidth);

            drawGrid(canvas);
            drawBlocks(canvas);
            drawNumbers(canvas);
            drawLetters(canvas);

        }

    }

    private void drawLetters(Canvas canvas) {

        if (letters != null) {

            float letterTextSize = (squareWidth / TEXT_LETTER_SCALE);
            gridTextPaint.setTextSize(letterTextSize * getResources().getDisplayMetrics().density);

            for (int y = 0; y < letters.length; ++y) {

                for (int x = 0; x < letters[y].length; ++x) {

                    String text = String.valueOf(letters[y][x]);
                    int width = (int)gridTextPaint.measureText(text);

                    int xBeginLetter = ((x * squareWidth) + xBegin) + ((squareWidth - width) / 2);
                    int yBeginLetter = (y * squareWidth) + yBegin;

                    StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

                    canvas.save();
                    canvas.translate(xBeginLetter, yBeginLetter);
                    staticLayout.draw(canvas);
                    canvas.restore();

                }

            }

        }

    }

    private void drawNumbers(Canvas canvas) {

        if (numbers != null) {

            float numberTextSize = (squareWidth / TEXT_NUMBER_SCALE);
            gridTextPaint.setTextSize(numberTextSize * getResources().getDisplayMetrics().density);

            for (int y = 0; y < numbers.length; ++y) {

                for (int x = 0; x < numbers[y].length; ++x) {

                    if (numbers[y][x] != 0) {

                        String text = String.valueOf(numbers[y][x]);
                        int width = (int) gridTextPaint.measureText(text);

                        int xBeginNumber = (x * squareWidth) + xBegin;
                        int yBeginNumber = (y * squareWidth) + yBegin;

                        StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

                        canvas.save();
                        canvas.translate(xBeginNumber, yBeginNumber);
                        staticLayout.draw(canvas);
                        canvas.restore();

                    }

                }

            }

        }

    }

    private void drawGrid(Canvas canvas) {

        if (gridWidth > 0 && gridHeight > 0) {

            // draw bounding rectangle

            canvas.drawRect(xBegin, yBegin, xEnd, yEnd, gridPaint);

            // draw grid lines (vertical)

            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(((i * squareWidth) + xBegin), yBegin, ((i * squareWidth) + xBegin), yEnd, gridPaint);
            }

            // draw grid lines (horizontal)

            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(xBegin, ((i * squareHeight) + yBegin), xEnd, ((i * squareHeight) + yBegin), gridPaint);
            }

        }

    }

    private void drawBlocks(Canvas canvas) {

        if (letters != null) {

            gridPaint.setStyle(Paint.Style.FILL);

            for (int y = 0; y < letters.length; ++y) {

                for (int x = 0; x < letters[y].length; ++x) {

                    if (letters[y][x] == BLOCK) {

                        int xBeginBlock = (x * squareWidth) + xBegin;
                        int yBeginBlock = (y * squareWidth) + yBegin;

                        int xEndBlock = (xBeginBlock + squareWidth);
                        int yEndBlock = (yBeginBlock + squareHeight);

                        canvas.drawRect(xBeginBlock, yBeginBlock, xEndBlock, yEndBlock, gridPaint);

                    }

                }

            }

            gridPaint.setStyle(Paint.Style.STROKE);

        }

    }



    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        Object value = evt.getNewValue();

        if (name.equals(CrosswordMagicController.GRID_LETTERS_PROPERTY)) {

            if (value instanceof Character[][]) {

                this.letters = (Character[][]) value;

                invalidate();

            }

        }

        if (name.equals(CrosswordMagicController.GRID_NUMBERS_PROPERTY)) {

            if (value instanceof Integer[][]) {

                this.numbers = (Integer[][]) value;

                invalidate();

            }

        }

        if (name.equals(CrosswordMagicController.GRID_DIMENSION_PROPERTY)) {

            if (value instanceof Integer[]) {

                Integer[] dimension = (Integer[]) value;

                this.gridHeight = dimension[0];
                this.gridWidth = dimension[1];

                invalidate();

            }

        }
        if (name.equals(CrosswordMagicController.GUESS_PROPERTY)) {
            if (evt.getNewValue() != null) { // Correct guess
                Toast.makeText(getContext(), R.string.correct_guess, Toast.LENGTH_SHORT).show();
            }
            else { // incorrect guess
                Toast.makeText(getContext(), R.string.incorrect_guess, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class OnTouchHandler implements View.OnTouchListener {

        private String userInput;
        private Context context;

        public OnTouchHandler(Context context) {
            this.context = context;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            int eventX = (int)event.getX();
            int eventY = (int)event.getY();

            if (eventX >= xBegin && eventX <= xEnd && eventY >= yBegin && eventY <= yEnd) {

                int x = ((eventX - xBegin) / squareWidth);
                int y = ((eventY - yBegin) / squareHeight);
                int n = numbers[y][x];
                if (n != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                    builder.setTitle(R.string.dialog_title);
                    final EditText input = new EditText(this.context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String userInput = input.getText().toString();
                            userInput = userInput.toUpperCase();
                            controller.tryGuess(userInput, n);




                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String userInput = "";
                            dialog.cancel();
                        }
                    });
                    AlertDialog aboutDialog = builder.show();


//                    final Dialog dialog = new Dialog(this.context);
//                    dialog.setContentView(R.layout.custom_alert_dialog);
//                    dialog.setTitle(R.string.dialog_title);
//                    dialog.setCancelable(false);
//                    EditText editText = (EditText) dialog.findViewById(R.id.guessInput) ;
//
//                    Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
//                    buttonCancel.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            isAcross = true;
//                            userInput = "";
//                            dialog.dismiss();
//                        }
//                    });
//
//                    Button buttonOK = (Button) dialog.findViewById(R.id.buttonOK);
//                    buttonOK.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            userInput = editText.getText().toString();
//                            String key;
//                            if (isAcross) {
//                                key = n + "D";
//                            }
//                            else {
//                                key = n + "A";
//                            }
//                            controller.tryGuess(n, userInput, key);
//                        }
//                    });
//
//                    Switch switchOrientation = (Switch) dialog.findViewById(R.id.switchOrientation);
//                    switchOrientation.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (switchOrientation.isChecked()) {
//                                isAcross = false;
//                                switchOrientation.setText(R.string.down);
//                            }
//                            else {
//                                isAcross = true;
//                                switchOrientation.setText(R.string.across);
//                            }
//                        }
//                    });
//                    dialog.show();
                }
            }
            return false;
        }

    }
}

