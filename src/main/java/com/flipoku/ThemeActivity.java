package com.flipoku;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.flipoku.Util.DialogCreator;
import com.flipoku.Util.StaticMethodsHelper;
import com.flipoku.Util.UserPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {
    //private static final String TAG = "flipoku";
    private DialogCreator dialogCreator;
    private String[] arrayColorMode;
    private AlertDialog currentColorDialog;
    private Toolbar themeToolbar;
    private Spinner themeSpinner;
    private Switch linesColorSwitch;
    private TextView linesColorTextView;
    private UserPreferencesHelper preferences;
    private TextView backgroundColorTextView;
    private LinearLayout backgroundColorLinearLayout;
    private TextView textColorTextView;
    private LinearLayout textColorLinearLayout;
    private LinearLayout boardPreviewLinearLayout;
    private LinearLayout[][] previewBoard;
    private LinearLayout boardPreviewSubgrid00;
    private LinearLayout boardPreviewSubgrid01;
    private LinearLayout boardPreviewSubgrid10;
    private LinearLayout boardPreviewSubgrid11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        dialogCreator = new DialogCreator(this, this.getLayoutInflater());
        arrayColorMode = new String[]{getResources().getString(R.string.mode_day), getResources().getString(R.string.mode_night), getResources().getString(R.string.mode_custom)};
        preferences = new UserPreferencesHelper(this);
        themeSpinner = (Spinner) findViewById(R.id.themeSpinner);
        themeToolbar = (Toolbar) findViewById(R.id.themeToolbar);
        linesColorSwitch = (Switch) findViewById(R.id.linesColorSwitch);
        linesColorTextView = (TextView) findViewById(R.id.linesColorSwitch);
        backgroundColorTextView = (TextView) findViewById(R.id.backgroundColorTextView);
        backgroundColorLinearLayout = (LinearLayout) findViewById(R.id.backgroundColorLinearLayout);
        textColorTextView = (TextView) findViewById(R.id.textColorTextView);
        textColorLinearLayout = (LinearLayout) findViewById(R.id.textColorLinearLayout);
        boardPreviewLinearLayout = (LinearLayout) findViewById(R.id.boardPreviewLinearLayout);
        boardPreviewSubgrid00 = (LinearLayout) findViewById(R.id.boardPreviewSubgrid00);
        boardPreviewSubgrid01 = (LinearLayout) findViewById(R.id.boardPreviewSubgrid01);
        boardPreviewSubgrid10 = (LinearLayout) findViewById(R.id.boardPreviewSubgrid10);
        boardPreviewSubgrid11 = (LinearLayout) findViewById(R.id.boardPreviewSubgrid11);
        previewBoard = new LinearLayout[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                String cellIdName = "cell" + i + "" + j;
                int resourceId = this.getResources().getIdentifier(cellIdName, "id", this.getPackageName());
                previewBoard[i][j] = (LinearLayout) findViewById(resourceId);
            }
        }
        setSupportActionBar(themeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        themeSpinner.setAdapter(getAdapter());
        addListeners();
        refreshView();
    }

    @SuppressLint("NewApi")
    private void refreshBoardPreview() {
        //agregamos cada uno de los colores al tablero
        boolean isLinesBlack = preferences.getLinesBlack();
        Drawable drawable;
        // drawable del borde del tablero
        if(isLinesBlack) {
            drawable = getResources().getDrawable(R.drawable.border_table);
        } else {
            drawable = getResources().getDrawable(R.drawable.border_table_night_mode);
        }
        boardPreviewLinearLayout.setBackground(drawable);
        // drawable de los bordes de los casilleros
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if(isLinesBlack) {
                    drawable = getResources().getDrawable(R.drawable.border_cell);
                    previewBoard[i][j].setBackground(drawable);
                } else {
                    drawable = getResources().getDrawable(R.drawable.border_cell_night_mode);
                    previewBoard[i][j].setBackground(drawable);
                }
            }
        }
        // drawable de los bordes de los subgrids
        if(isLinesBlack) {
            drawable = getResources().getDrawable(R.drawable.border_subgrid);
        } else {
            drawable = getResources().getDrawable(R.drawable.border_subgrid_night_mode);
        }
        boardPreviewSubgrid00.setBackground(drawable);
        boardPreviewSubgrid01.setBackground(drawable);
        boardPreviewSubgrid10.setBackground(drawable);
        boardPreviewSubgrid11.setBackground(drawable);

        // drawable de los bordes de la seleccion
        drawable = getResources().getDrawable(R.drawable.border_selected_cell);
        previewBoard[4][4].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_cross);
        previewBoard[2][1].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_row);
        previewBoard[2][4].setBackground(drawable);
        previewBoard[2][3].setBackground(drawable);
        previewBoard[2][2].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_row_left);
        previewBoard[2][0].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_row_right);
        previewBoard[2][5].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_column);
        previewBoard[1][1].setBackground(drawable);
        previewBoard[3][1].setBackground(drawable);
        previewBoard[4][1].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_column_top);
        previewBoard[0][1].setBackground(drawable);
        drawable = getResources().getDrawable(R.drawable.border_selected_column_bottom);
        previewBoard[5][1].setBackground(drawable);

        // color de las letras
        List<TextView> childTextViewList = new ArrayList<>();
        childTextViewList.add((TextView) previewBoard[0][1].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[0][2].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[0][3].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[0][4].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[1][0].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[1][1].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[1][4].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[2][2].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[2][5].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[3][0].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[3][3].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[4][1].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[4][4].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[5][2].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[5][3].getChildAt(0));
        childTextViewList.add((TextView) previewBoard[5][5].getChildAt(0));
        for(TextView each : childTextViewList) {
            each.setTextColor(preferences.getTextColor());
            each.setBackgroundColor(0);
        }
        // color del fondo del tablero
        boardPreviewLinearLayout.setBackgroundColor(preferences.getBackgroundColor());
    }

    private void addListeners() {
        themeSpinner.post(new Runnable() {// esto se hace para que no se active el listener al crear el layout, ya que se agrega luego de creado
            public void run() {
                themeSpinner.setOnItemSelectedListener(getListenerThemeSpinner());
                themeSpinner.setSelection(preferences.getColorModeIndex());
            }
        });
        linesColorSwitch.setOnCheckedChangeListener(getListenerLinesColorSwitch());
    }

    private CompoundButton.OnCheckedChangeListener getListenerLinesColorSwitch() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.saveLinesBlack(isChecked);
                // tambien cambiamos el modo a personalizado
                preferences.saveColorModeIndex(2);
                refreshView();
            }
        };
    }

    private AdapterView.OnItemSelectedListener getListenerThemeSpinner() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferences.saveColorModeIndex(position);
                changeColorsForTheme();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void changeColorsForTheme() {
        // si se selecciono personalizado, no pasa nada
        // en caso de q la seleccion sea diurno o nocturno, se modifican los colores en base a la seleccion y se hace refresh
        int currentTheme = preferences.getColorModeIndex();
        if (currentTheme == 2) {
            return;
        } else if (currentTheme == 1) {//nocturno
            setColorMode(true);
        } else if (currentTheme == 0) {//diurno
            setColorMode(false);
        }
        refreshView();
    }

    private void setColorMode(boolean isDark) {
        if (!isDark) {
            int[] arrayColorThemeBright = {getResources().getColor(R.color.colorBackground), getResources().getColor(R.color.colorText)};
            saveColorMode(arrayColorThemeBright, true);
        } else {
            int[] arrayColorThemeDark = {getResources().getColor(R.color.colorBackgroundDark), getResources().getColor(R.color.colorTextDark)};
            saveColorMode(arrayColorThemeDark, false);
        }
    }

    private void saveColorMode(int[] arrayColorTheme, boolean isLinesBlack) {
        preferences.saveBackgroundColor(arrayColorTheme[0]);
        preferences.saveTextColor(arrayColorTheme[1]);
        preferences.saveLinesBlack(isLinesBlack);
    }

    private void refreshView() {
        themeSpinner.setSelection(preferences.getColorModeIndex());
        boolean isLinesBlack = preferences.getLinesBlack();
        StaticMethodsHelper.setSwitchValues(isLinesBlack, linesColorSwitch, linesColorTextView, getResources().getString(R.string.lines_black), getResources().getString(R.string.lines_white));
        setColorValues(preferences.getBackgroundColor(), backgroundColorTextView, backgroundColorLinearLayout);
        setColorValues(preferences.getTextColor(), textColorTextView, textColorLinearLayout);
        refreshBoardPreview();
    }

    private void setColorValues(int color, TextView textView, LinearLayout linearLayout) {
        textView.setText("" + color);
        linearLayout.setBackgroundColor(color);
    }

    public void backgroundColorOnClick(View view) {
        Handler colorChangedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                preferences.saveBackgroundColor((int) msg.getData().getSerializable("numberColor"));
                // tambien cambiamos el modo a personalizado
                preferences.saveColorModeIndex(2);
                closeDialogAndRefresh();
            }
        };
        currentColorDialog = dialogCreator.createDialogColorSelection(preferences.getBackgroundColor(), colorChangedHandler);
        currentColorDialog.show();
    }

    public void textColorOnClick(View view) {
        Handler colorChangedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                preferences.saveTextColor((int) msg.getData().getSerializable("numberColor"));
                // tambien cambiamos el modo a personalizado
                preferences.saveColorModeIndex(2);
                closeDialogAndRefresh();
            }
        };
        currentColorDialog = dialogCreator.createDialogColorSelection(preferences.getTextColor(), colorChangedHandler);
        currentColorDialog.show();
    }

    private void closeDialogAndRefresh() {
        currentColorDialog.dismiss();
        currentColorDialog = null;
        refreshView();
    }

    private ArrayAdapter getAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(arrayColorMode));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}