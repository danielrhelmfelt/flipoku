package com.flipoku.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipoku.Domain.Board;
import com.flipoku.R;

import java.io.Serializable;

public class DialogCreator {
    //private static final String TAG = "flipoku";
    private Context context;
    private LayoutInflater layoutInflater;

    public DialogCreator(Context context, LayoutInflater layoutInflater) {
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    /*public AlertDialog createDialogColorPicker(int color, Handler handler) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = new ColorPickerView(context, color, handler);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        return alertDialog;
    }*/

    /*public AlertDialog createDialogLoadingSpinner() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_loading_spinner, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        return alertDialog;
    }*/

    public AlertDialog createDialogAreYouSure(Handler yesHandler, Handler noHandler, String yesButtonText, String noButtonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_are_you_sure, null);
        dialogBuilder.setView(dialogView);
        final Button yesButton = (Button) dialogView.findViewById(R.id.yesButton);
        final Button noButton = (Button) dialogView.findViewById(R.id.noButton);
        yesButton.setText(yesButtonText);
        noButton.setText(noButtonText);
        AlertDialog alertDialog = dialogBuilder.create();
        yesButton.setOnClickListener(getGenericListener(yesHandler, alertDialog));
        yesButton.setOnClickListener(getGenericListener(noHandler, alertDialog));
        return alertDialog;
    }

    private View.OnClickListener getGenericListener(final Handler handler, final AlertDialog alertDialog) {
        //devuelve un listener que activa el handler al ser clickeado el boton
        return new View.OnClickListener() {
            public void onClick(View v) {
                //si el handles es null, no hace nada
                if(handler != null) {
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);
                }
                alertDialog.dismiss();
            }
        };
    }


    public AlertDialog createDialogNewGame(Handler newGameHandler) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_new_game, null);
        dialogBuilder.setView(dialogView);
        Button[] listButton = new Button[4];
        final Button easyRadioButton = (Button) dialogView.findViewById(R.id.easyButton);
        easyRadioButton.setTag(Board.CLUES_EASY);
        listButton[0] = easyRadioButton;
        final Button mediumRadioButton = (Button) dialogView.findViewById(R.id.mediumButton);
        mediumRadioButton.setTag(Board.CLUES_MEDIUM);
        listButton[1] = mediumRadioButton;
        final Button hardRadioButton = (Button) dialogView.findViewById(R.id.hardButton);
        hardRadioButton.setTag(Board.CLUES_HARD);
        listButton[2] = hardRadioButton;
        final Button expertRadioButton = (Button) dialogView.findViewById(R.id.expertButton);
        expertRadioButton.setTag(Board.CLUES_EXPERT);
        listButton[3] = expertRadioButton;
        final NumberPicker cluesNumberPicker = (NumberPicker) dialogView.findViewById(R.id.cluesNumberPicker);
        cluesNumberPicker.setMinValue(Board.CLUES_MIN);
        cluesNumberPicker.setMaxValue(Board.CLUES_MAX);
        cluesNumberPicker.setWrapSelectorWheel(false);
        cluesNumberPicker.setValue(cluesNumberPicker.getMaxValue());
        final LinearLayout customGameLinearLayout = (LinearLayout) dialogView.findViewById(R.id.customGameLinearLayout);
        final LinearLayout newGameButtonsLinearLayout = (LinearLayout) dialogView.findViewById(R.id.newGameButtonsLinearLayout);
        final Button customizedButton = (Button) dialogView.findViewById(R.id.customizedButton);
        final Button returnButton = (Button) dialogView.findViewById(R.id.returnButton);
        final Button playCustomButton = (Button) dialogView.findViewById(R.id.playCustomButton);
        AlertDialog alertDialog = dialogBuilder.create();
        setListenerToButtons(listButton, newGameHandler, alertDialog);
        playCustomButton.setOnClickListener(getButtonCustomListener(cluesNumberPicker, newGameHandler, alertDialog));
        setListenerToShowCustomBlock(customizedButton, returnButton, customGameLinearLayout, newGameButtonsLinearLayout);
        return alertDialog;
    }

    private void setListenerToShowCustomBlock(final Button button, final Button returnButton, final LinearLayout linearLayout, final LinearLayout newGameButtonsLinearLayout) {
        // agrega un listener para que en caso de ser presionado el boton, se haga visible el bloque de juego personalizado
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                newGameButtonsLinearLayout.setVisibility(View.GONE);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                newGameButtonsLinearLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setListenerToButtons(Button[] listButton, final Handler handler, AlertDialog alertDialog) {
        for (int i = 0; i < listButton.length; i++) {
            listButton[i].setOnClickListener(getButtonListener(handler, alertDialog));
        }
    }

    private View.OnClickListener getButtonListener(final Handler handler, final AlertDialog alertDialog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int numberClues = Integer.parseInt(v.getTag().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("numberClues", numberClues);
                StaticMethodsHelper.executeBundleOnThread(handler, bundle, alertDialog);
            }
        };
    }

    private View.OnClickListener getButtonCustomListener(final NumberPicker cluesNumberPicker, final Handler handler, final AlertDialog alertDialog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int numberClues = cluesNumberPicker.getValue();
                Bundle bundle = new Bundle();
                bundle.putSerializable("numberClues", numberClues);
                StaticMethodsHelper.executeBundleOnThread(handler, bundle, alertDialog);
            }
        };
    }

    public AlertDialog createDialogColorSelection(int currentColor, Handler handler) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_color_selection, null);
        dialogBuilder.setView(dialogView);
        Button selectColorButton = (Button) dialogView.findViewById(R.id.selectColorButton);
        EditText selectedColorEditText = (EditText) dialogView.findViewById(R.id.selectedColorEditText);
        LinearLayout showColorLinearLayout = (LinearLayout) dialogView.findViewById(R.id.showColorLinearLayout);

        selectedColorEditText.addTextChangedListener(getColorTextChangedListener());
        TextView[] arrayTextView = new TextView[4];
        arrayTextView[0] = (TextView) dialogView.findViewById(R.id.brightnessTextView);
        arrayTextView[1] = (TextView) dialogView.findViewById(R.id.redTextView);
        arrayTextView[2] = (TextView) dialogView.findViewById(R.id.greenTextView);
        arrayTextView[3] = (TextView) dialogView.findViewById(R.id.blueTextView);
        SeekBar[] arraySeekBar = new SeekBar[4];
        arraySeekBar[0] = (SeekBar) dialogView.findViewById(R.id.brightnessSeekBar);
        arraySeekBar[0].setTag(0);
        arraySeekBar[0].setOnSeekBarChangeListener(getSeekBarListener(arraySeekBar, arrayTextView[0], showColorLinearLayout, selectedColorEditText));
        arraySeekBar[1] = (SeekBar) dialogView.findViewById(R.id.redSeekBar);
        arraySeekBar[1].setTag(1);
        arraySeekBar[1].setOnSeekBarChangeListener(getSeekBarListener(arraySeekBar, arrayTextView[1], showColorLinearLayout, selectedColorEditText));
        arraySeekBar[2] = (SeekBar) dialogView.findViewById(R.id.greenSeekBar);
        arraySeekBar[2].setTag(2);
        arraySeekBar[2].setOnSeekBarChangeListener(getSeekBarListener(arraySeekBar, arrayTextView[2], showColorLinearLayout, selectedColorEditText));
        arraySeekBar[3] = (SeekBar) dialogView.findViewById(R.id.blueSeekBar);
        arraySeekBar[3].setTag(3);
        arraySeekBar[3].setOnSeekBarChangeListener(getSeekBarListener(arraySeekBar, arrayTextView[3], showColorLinearLayout, selectedColorEditText));

        refreshColorSelectionView(currentColor, arrayTextView, arraySeekBar, showColorLinearLayout, selectedColorEditText);
        selectColorButton.setOnClickListener(getSelectColorButtonListener(arraySeekBar, handler));
        AlertDialog alertDialog = dialogBuilder.create();
        return alertDialog;
    }

    private void refreshColorSelectionView(int currentColor, TextView[] arrayTextView, SeekBar[] arraySeekBar, LinearLayout showColorLinearLayout, EditText selectedColorEditText) {
        ColorDrawable currentColorDrawable = new ColorDrawable(currentColor);
        int color = currentColorDrawable.getColor();
        int A = currentColorDrawable.getAlpha();
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);
        arrayTextView[0].setText("" + A);
        arrayTextView[1].setText("" + R);
        arrayTextView[2].setText("" + G);
        arrayTextView[3].setText("" + B);
        arraySeekBar[0].setProgress(A);
        arraySeekBar[1].setProgress(R);
        arraySeekBar[2].setProgress(G);
        arraySeekBar[3].setProgress(B);
        showColorLinearLayout.setBackgroundColor(color);
        selectedColorEditText.setText("0x" + String.format("%02x", A) + String.format("%02x", R) + String.format("%02x", G) + String.format("%02x", B));
    }

    private View.OnClickListener getSelectColorButtonListener(final SeekBar[] arraySeekBar, final Handler colorChangedHandler) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("numberColor", getArgbColor(arraySeekBar));
                Message message = colorChangedHandler.obtainMessage();
                message.setData(bundle);
                colorChangedHandler.sendMessage(message);
            }
        };
    }

    private TextWatcher getColorTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // se actualiza en base al valor nuevo
                String value = s.toString();
            }
        };
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarListener(final SeekBar[] arraySeekBar, final TextView textView, final LinearLayout linearLayout, final EditText editText) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("" + progress);
                onSeekBarProgressChanged(seekBar, progress, arraySeekBar, linearLayout, editText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private void onSeekBarProgressChanged(SeekBar seekBar, int progress, SeekBar[] arraySeekBar, LinearLayout linearLayout, EditText editText) {
        //get current ARGB values
        int A = arraySeekBar[0].getProgress();
        int R = arraySeekBar[1].getProgress();
        int G = arraySeekBar[2].getProgress();
        int B = arraySeekBar[3].getProgress();
        //Reference the value changing
        int seekBarTag = (int) seekBar.getTag();
        //Get the chnaged value
        if (seekBarTag == 0)
            A = progress;
        else if (seekBarTag == 1)
            R = progress;
        else if (seekBarTag == 2)
            G = progress;
        else if (seekBarTag == 3)
            B = progress;
        //Build and show the new color
        linearLayout.setBackgroundColor(Color.argb(A, R, G, B));
        //show the color value
        editText.setText("0x" + String.format("%02x", A) + String.format("%02x", R) + String.format("%02x", G) + String.format("%02x", B));
        //int color = Color.argb(A, R, G, B);
        //Log.i(TAG, "Se modifica el COLOR a: " + color);
    }

    private int getArgbColor(SeekBar[] arraySeekBar) {
        //get current ARGB values
        int A = arraySeekBar[0].getProgress();
        int R = arraySeekBar[1].getProgress();
        int G = arraySeekBar[2].getProgress();
        int B = arraySeekBar[3].getProgress();
        int color = Color.argb(A, R, G, B);
        return color;
    }
}