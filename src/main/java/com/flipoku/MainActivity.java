package com.flipoku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipoku.Domain.Board;
import com.flipoku.Domain.Statistics;
import com.flipoku.Repository.StatisticsRepository;
import com.flipoku.Util.DialogCreator;
import com.flipoku.Util.UserPreferencesHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //private static final String TAG = "flipoku";
    private UserPreferencesHelper preferences;
    private Board currentBoard;
    private int[] selectedCell;
    private Chronometer timerChronometer;
    private TextView quantityHintTextView, mistakesTextView, difficultyTextView, finishedGameTextView;
    private LinearLayout[][] viewBoard;
    private LinearLayout[][] viewSubgrid;
    private Button[] viewTiles;
    private DialogCreator dialogCreator;
    private boolean isChronometerRunning;
    private LinearLayout finishedGameLinearLayout, tilesLinearLayout, boardLinearLayout;
    private DrawerLayout drawer;

    @SuppressLint("HandlerLeak")
    private Handler newGameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int numberClues = (Integer) msg.getData().getSerializable("numberClues");
            startNewGame(numberClues);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new UserPreferencesHelper(this);
        dialogCreator = new DialogCreator(this, this.getLayoutInflater());
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        timerChronometer = (Chronometer) findViewById(R.id.timerChronometer);
        quantityHintTextView = (TextView) findViewById(R.id.quantityHintTextView);
        mistakesTextView = (TextView) findViewById(R.id.mistakesTextView);
        difficultyTextView = (TextView) findViewById(R.id.difficultyTextView);
        finishedGameTextView = (TextView) findViewById(R.id.finishedGameTextView);
        finishedGameLinearLayout = (LinearLayout) findViewById(R.id.finishedGameLinearLayout);
        tilesLinearLayout = (LinearLayout) findViewById(R.id.tilesLinearLayout);
        boardLinearLayout = (LinearLayout) findViewById(R.id.boardLinearLayout);
        viewBoard = new LinearLayout[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellIdName = "cell" + i + "" + j;
                int resourceId = this.getResources().getIdentifier(cellIdName, "id", this.getPackageName());
                viewBoard[i][j] = (LinearLayout) findViewById(resourceId);
            }
        }
        viewSubgrid = new LinearLayout[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String subgridIdName = "subgrid" + i + "" + j;
                int resourceId = this.getResources().getIdentifier(subgridIdName, "id", this.getPackageName());
                viewSubgrid[i][j] = (LinearLayout) findViewById(resourceId);
            }
        }
        viewTiles = new Button[9];
        for (int i = 0; i < 9; i++) {
            int number = i + 1;
            String tileButtonName = "tile" + number;
            int resourceId = this.getResources().getIdentifier(tileButtonName, "id", this.getPackageName());
            viewTiles[i] = (Button) findViewById(resourceId);
        }
        timerChronometer.setText(getResources().getString(R.string.time));
        timerChronometer.setFormat(getResources().getString(R.string.time) + " %s");
        mistakesTextView.setText(getResources().getString(R.string.mistakes));
        currentBoard = null;
        selectedCell = null;
        isChronometerRunning = false;
        boardLinearLayout.post(new Runnable() {// esto se hace para que no se active el listener al crear el layout, ya que se agrega luego de creado
            public void run() {
                refreshView();
            }
        });
    }

    private void startNewGame(int numberClues) {
        currentBoard = new Board(numberClues);
        selectedCell = null;
        isChronometerRunning = true;
        timerChronometer.setBase(SystemClock.elapsedRealtime());
        timerChronometer.start();
        refreshView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "Se ingresa a estado STOP");
        if (isChronometerRunning) {
            // hacedanielmos que se guarde el boolean
            preferences.saveChronometerRunning(isChronometerRunning);
            long elapsedTime = SystemClock.elapsedRealtime() - timerChronometer.getBase();
            currentBoard.setElapsedTimeMilis(elapsedTime);
            preferences.savePlayingBoard(currentBoard);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i(TAG, "Se ingresa a estado RESUME");
        if (isChronometerRunning()) {
            // se busca el tablero actual
            Board previousBoard = preferences.getPlayingBoard();
            // si es null se cancela el cronometro
            if (previousBoard == null) {
                preferences.saveChronometerRunning(false);
                return;
            } else {
                continueGame(previousBoard);
            }
        }
    }

    private boolean isChronometerRunning() {
        // busca si esta funcionando en cronometro
        boolean isRunning = preferences.getChronometerRunning();
        return isRunning;
    }

    private void continueGame(Board previousBoard) {
        currentBoard = previousBoard;
        selectedCell = null;
        isChronometerRunning = true;
        timerChronometer.setBase(SystemClock.elapsedRealtime() - previousBoard.getElapsedTimeMilis());
        timerChronometer.start();
        refreshView();
    }

    private void onFinishGame() {
        if (isChronometerRunning) {
            isChronometerRunning = false;
            preferences.saveChronometerRunning(false);
            long timeWhenStopped = SystemClock.elapsedRealtime() - timerChronometer.getBase();
            timerChronometer.stop();
            String text;
            int victory = 0;
            if (currentBoard.getNumberMistakes() <= 3) {
                text = getResources().getString(R.string.victory);
                victory = victory + 1;
            } else {
                text = getResources().getString(R.string.defeat);
            }
            finishedGameTextView.setText(text);
            setFinishedGameVisibility(true);
            currentBoard.setElapsedTimeMilis(timeWhenStopped);
            // buscamos el Statistics que corresponda con la dificultad actual, y actualizamos los valores, en caso de no existir, se crea uno nuevo
            StatisticsRepository statisticsRepository = Statistics.getRepository(this);
            Statistics statistics = statisticsRepository.findByClues(currentBoard.getNumberClues());
            if (statistics == null) {
                statistics = new Statistics(currentBoard.getNumberClues(), 0, 0, 0, 0);
            }
            statistics.setWins(statistics.getWins() + victory);
            // no aumentamos las derrotas ya que se aumenta al momento de perder
            //statistics.setLoses(statistics.getLoses() + defeat);
            if (victory == 1) {
                statistics.setStreak(statistics.getStreak() + 1);
                // solo actualizamos el BestTime si es una victoria
                if (statistics.getBestTime() == 0) {// en caso de que este guardado el valor 0 como minimo
                    statistics.setBestTime(currentBoard.getElapsedTimeMilis());
                } else {
                    if (statistics.getBestTime() > currentBoard.getElapsedTimeMilis()) {
                        statistics.setBestTime(currentBoard.getElapsedTimeMilis());
                    }
                }
            }
            statisticsRepository.save(statistics);
        }
    }

    public void cellOnClick(View view) {
        if (currentBoard == null) {
            Toast.makeText(this, getResources().getString(R.string.must_start_new_game), Toast.LENGTH_SHORT).show();
            return;
        }
        LinearLayout cell = (LinearLayout) view;
        selectedCell = getArrayRowColumn((String) cell.getTag());
        //Toast.makeText(this, "FILA " + i + " COL " + j + " Valor " + selectedCellValue, Toast.LENGTH_SHORT).show();//coordinate
        refreshView();
    }

    public void tileOnClick(View view) {
        Button button = (Button) view;
        //Toast.makeText(this, "Numero seleccionado: " + button.getText(), Toast.LENGTH_SHORT).show();
        // si la celda seleccionada esta vacia, se verifica que el numero presionado sea el q corresponde, en caso contrario se aumenta el nro de errores
        if (selectedCell != null) {
            if (currentBoard.getDiscoveredMatrix()[selectedCell[0]][selectedCell[1]] == false) {// si no esta visible
                // verifica que el numero presionado sea el q corresponde
                int selectedCellRealValue = currentBoard.getSudokuBoardMatrix()[selectedCell[0]][selectedCell[1]];
                if (Integer.parseInt("" + button.getText()) == selectedCellRealValue) {
                    currentBoard.getDiscoveredMatrix()[selectedCell[0]][selectedCell[1]] = true;
                } else {
                    currentBoard.setNumberMistakes(currentBoard.getNumberMistakes() + 1);
                    //Toast.makeText(this, "¡Número equivocado!", Toast.LENGTH_SHORT).show();
                    showMessage(this.getResources().getString(R.string.wrong_number));
                    // verificamos si se supero el numero de errores y aumentamos las derrotas de ser asi
                    if (currentBoard.getNumberMistakes() == 4) {// solo se aumenta una vez
                        updateNumberLoses();
                    }
                }
            }
        }
        refreshView();
    }

    private void updateNumberLoses() {
        // aumenta en uno el numero de derrotas y resetea la racha de victorias
        StatisticsRepository statisticsRepository = Statistics.getRepository(this);
        Statistics statistics = statisticsRepository.findByClues(currentBoard.getNumberClues());
        if (statistics == null) {
            statistics = new Statistics(currentBoard.getNumberClues(), 0, 0, 0, 0);
        }
        statistics.setLoses(statistics.getLoses() + 1);
        statistics.setStreak(0);
        statisticsRepository.save(statistics);

    }

    public void hintOnClick(View view) {
        //Toast.makeText(this, "Indicio seleccionado", Toast.LENGTH_SHORT).show();
        if (selectedCell != null) {
            if (currentBoard.getDiscoveredMatrix()[selectedCell[0]][selectedCell[1]] == false) {// si no esta visible
                // si existen aun indicios
                if (currentBoard.getHintsRemaining() > 0) {
                    int selectedCellRealValue = currentBoard.getSudokuBoardMatrix()[selectedCell[0]][selectedCell[1]];
                    //Toast.makeText(this, "El valor de la celda es: " + selectedCellRealValue, Toast.LENGTH_SHORT).show();
                    currentBoard.setHintsRemaining(currentBoard.getHintsRemaining() - 1);
                    showMessage(getResources().getString(R.string.value_of_the_cell) + " " + selectedCellRealValue);
                }
            }
        }
        refreshView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawer.openDrawer(Gravity.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_start_new_game) {
            dialogCreator.createDialogNewGame(newGameHandler).show();
        } else if (id == R.id.nav_statistics) {
            Intent i = new Intent(this, StatisticsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_theme) {
            Intent i = new Intent(this, ThemeActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_exit) {
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("NewApi")
    private void clearBoard() {
        boolean isLinesBlack = preferences.getLinesBlack();
        Drawable drawable;
        // vuelve a poner los colores originales al tablero
        if (isLinesBlack) {
            drawable = this.getResources().getDrawable(R.drawable.border_table);
        } else {
            drawable = this.getResources().getDrawable(R.drawable.border_table_night_mode);
        }
        boardLinearLayout.setBackground(drawable);
        boardLinearLayout.setBackgroundColor(preferences.getBackgroundColor());
        if (isLinesBlack) {
            drawable = this.getResources().getDrawable(R.drawable.border_subgrid);
        } else {
            drawable = this.getResources().getDrawable(R.drawable.border_subgrid_night_mode);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                viewSubgrid[i][j].setBackground(drawable);
            }
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                // color del borde del tablero
                if (isLinesBlack) {
                    drawable = this.getResources().getDrawable(R.drawable.border_cell);
                } else {
                    drawable = this.getResources().getDrawable(R.drawable.border_cell_night_mode);
                }
                viewBoard[x][y].setBackground(drawable);
                if (viewBoard[x][y].getChildAt(0) != null) {
                    //viewBoard[x][y].getChildAt(0).setBackground(this.getResources().getDrawable(R.drawable.border_cell));
                    //viewBoard[x][y].getChildAt(0).setBackgroundResource(0);
                    viewBoard[x][y].removeAllViews();
                }
            }
        }
    }

    private void clearTiles() {// hace que todos los botones de los numeros sean visibles
        for (int i = 0; i < viewTiles.length; i++) {
            Button tile = (Button) viewTiles[i];
            tile.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    private void refreshView() {
        clearBoard();// se limpia el tablero (se vuelve a dibujar)
        clearTiles();
        if (preferences.getTimerVisibility()) {
            timerChronometer.setVisibility(View.VISIBLE);
        } else {
            timerChronometer.setVisibility(View.INVISIBLE);
        }
        if (currentBoard != null) {
            quantityHintTextView.setText("x " + currentBoard.getHintsRemaining());
            mistakesTextView.setText(getResources().getString(R.string.mistakes) + " " + currentBoard.getNumberMistakes());
            difficultyTextView.setText(currentBoard.getDifficultyText(this));
            int[][] board = currentBoard.getSudokuBoardMatrix();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    // se muestra el numero si la celda esta visible
                    if (currentBoard.getDiscoveredMatrix()[i][j]) {
                        LinearLayout eachCell = viewBoard[i][j];
                        eachCell.removeAllViews();
                        TextView programmaticallyTextView = new TextView(this);
                        programmaticallyTextView.setText(board[i][j] + "");
                        programmaticallyTextView.setTextSize(22);
                        programmaticallyTextView.setTextColor(preferences.getTextColor());
                        programmaticallyTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        programmaticallyTextView.setGravity(Gravity.CENTER);
                        programmaticallyTextView.setWidth(eachCell.getWidth());
                        programmaticallyTextView.setHeight(eachCell.getHeight());
                        eachCell.addView(programmaticallyTextView);
                    }
                }
            }
            if (selectedCell != null) {
                if (currentBoard.getDiscoveredMatrix()[selectedCell[0]][selectedCell[1]]) {
                    // agregamos el fondo de seleccion a todos los mismos numeros de la celda seleccionada
                    int selectedCellValue = currentBoard.getSudokuBoardMatrix()[selectedCell[0]][selectedCell[1]];
                    addBackgroundCells(selectedCellValue);
                } else {
                    // si la celda seleccionada no esta descubierta, se aplica el background de fila y columna
                    addBackgroundColRow(selectedCell[0], selectedCell[1]);
                }
            }
            // se deshabilitan los botones de los numeros que ya han sido totalmente descubiertos
            doDisableTiles();
            if (isBoardCompleted()) {
                onFinishGame();
            } else {
                setFinishedGameVisibility(false);
                /*if (!isPencilActivated) {
                    //pencilButtonLinearLayout.setBackgroundColor(this.getResources().getColor(R.color.colorBackground));
                    pencilButtonLinearLayout.setBackground(this.getResources().getDrawable(R.drawable.border_button));
                } else {
                    pencilButtonLinearLayout.setBackgroundColor(this.getResources().getColor(R.color.colorPencilText));
                }*/
            }
        }
    }

    private void doDisableTiles() {
        int[][] boardMatrix = currentBoard.getSudokuBoardMatrix();
        int[] tilesArray = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (currentBoard.getDiscoveredMatrix()[i][j]) {
                    int numberAtPosition = boardMatrix[i][j];
                    tilesArray[numberAtPosition - 1] += 1;
                }
            }
        }
        for (int i = 0; i < tilesArray.length; i++) {
            Button tile = (Button) viewTiles[i];
            if (tilesArray[i] == 9) {
                tile.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean isBoardCompleted() {
        // si la matriz de descubiertos posee solo verdaderos
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (currentBoard.getDiscoveredMatrix()[i][j] == false) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("NewApi")
    private void addBackgroundColRow(int i, int j) {
        // primero se agregan los limites y el centro
        viewBoard[i][0].setBackground(this.getResources().getDrawable(R.drawable.border_selected_row_left));
        viewBoard[i][8].setBackground(this.getResources().getDrawable(R.drawable.border_selected_row_right));
        viewBoard[0][j].setBackground(this.getResources().getDrawable(R.drawable.border_selected_column_top));
        viewBoard[8][j].setBackground(this.getResources().getDrawable(R.drawable.border_selected_column_bottom));
        viewBoard[i][j].setBackground(this.getResources().getDrawable(R.drawable.border_selected_cross));
        for (int count = 1; count < 8; count++) {
            if (i != count) {
                LinearLayout eachCellRow = viewBoard[count][j];
                eachCellRow.setBackground(this.getResources().getDrawable(R.drawable.border_selected_column));
            }
            if (j != count) {
                LinearLayout eachCellCol = viewBoard[i][count];
                eachCellCol.setBackground(this.getResources().getDrawable(R.drawable.border_selected_row));
            }
        }
    }

    @SuppressLint("NewApi")
    private void addBackgroundCells(int selectedCellValue) {
        // recorremos el tablero y agregamos el fondo donde exista el mismo numero
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (currentBoard.getSudokuBoardMatrix()[x][y] == selectedCellValue) {
                    // si la celda esta oculta, no se aplica el background
                    if (currentBoard.getDiscoveredMatrix()[x][y]) {
                        LinearLayout eachCell = viewBoard[x][y];
                        TextView childTextView = (TextView) eachCell.getChildAt(0);
                        childTextView.setBackground(this.getResources().getDrawable(R.drawable.border_selected_cell));
                    }
                }
            }
        }
    }

    private int[] getArrayRowColumn(String value) {
        int[] arrayRowColumn = new int[2];
        arrayRowColumn[0] = Integer.parseInt(value.substring(0, 1));
        arrayRowColumn[1] = Integer.parseInt(value.substring(1));
        return arrayRowColumn;
    }

    public void difficultyOnClick(View view) {
        if (currentBoard != null) {
            Toast.makeText(this, getResources().getString(R.string.iterations_required) + " " + currentBoard.getCountToGenerate(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setFinishedGameVisibility(boolean isFinished) {
        if (isFinished) {
            tilesLinearLayout.setVisibility(View.GONE);
            finishedGameLinearLayout.setVisibility(View.VISIBLE);
        } else {
            finishedGameTextView.setText(null);
            finishedGameLinearLayout.setVisibility(View.GONE);
            tilesLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void drawerButtonOnClick(View view) {
        drawer.openDrawer(Gravity.LEFT, true);
    }

    private void showMessage(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage(msg);
        alert.show();
    }
}
