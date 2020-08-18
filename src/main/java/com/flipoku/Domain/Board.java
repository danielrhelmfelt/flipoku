package com.flipoku.Domain;

import android.content.Context;

import com.flipoku.R;
import com.flipoku.Repository.BoardRepository;
import com.flipoku.Repository.RepositoryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board extends RepositoryEntity {
    /*Repository Static Structure*/
    private static final long serialVersionUID = 1L;
    public static Board clone(Board obj) {
        try {
            return (Board) obj.clone();
        } catch (CloneNotSupportedException e) {/*not possible*/}
        return null;
    }

    public static BoardRepository getRepository(Context context) {
        return new BoardRepository(context);
    }
    /*Repository Static Structure*/

    public final static int CLUES_MIN = 17;
    public final static int CLUES_MAX = 71;
    public final static int CLUES_EASY = 50;
    public final static int CLUES_MEDIUM = 40;
    public final static int CLUES_HARD = 30;
    public final static int CLUES_EXPERT = 22;

    private long elapsedTimeMilis;
    private int hintsRemaining;
    private int numberMistakes;
    private boolean[][] cluesMatrix;
    private boolean[][] discoveredMatrix;
    private int[][] sudokuBoardMatrix;
    private List<Integer> listOneToNine;
    private int countToGenerate;

    public Board(int numberClues) {
        listOneToNine = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            listOneToNine.add(i);
        }
        elapsedTimeMilis = 0;
        hintsRemaining = 3;
        numberMistakes = 0;
        cluesMatrix = cluesMatrixGeneration(numberClues);
        discoveredMatrix = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                discoveredMatrix[i][j] = cluesMatrix[i][j];
            }
        }
        sudokuBoardMatrix = findValidBoard();
    }

    private boolean[][] cluesMatrixGeneration(int numberClues) {
        boolean[][] clues = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                clues[i][j] = false;
            }
        }
        for (int i = 0; i < numberClues; i++) {
            Random rd = new Random();
            int iRandom = rd.nextInt(9);
            int jRandom = rd.nextInt(9);
            while (clues[iRandom][jRandom] == true) {
                // buscamos otra posicion random
                iRandom = rd.nextInt(9);
                jRandom = rd.nextInt(9);
            }
            clues[iRandom][jRandom] = true;
        }
        return clues;
    }

    private int[][] findValidBoard() {
        int[][] newBoard = getNewBoard();
        int count = 0;
        while (!isBoardValid(newBoard)) {// && count < 1025
            newBoard = getNewBoard();
            count++;
        }
        countToGenerate = count;
        return newBoard;
    }

    private boolean isBoardValid(int[][] sudokuBoard) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuBoard[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getCountToGenerate() {
        return countToGenerate;
    }

    public int[][] getSudokuBoardMatrix() {
        return sudokuBoardMatrix;
    }

    public long getElapsedTimeMilis() {
        return elapsedTimeMilis;
    }

    public void setElapsedTimeMilis(long elapsedTime) {
        this.elapsedTimeMilis = elapsedTime;
    }

    public int getHintsRemaining() {
        return hintsRemaining;
    }

    public void setHintsRemaining(int hintsRemaining) {
        this.hintsRemaining = hintsRemaining;
    }

    public int getNumberMistakes() {
        return numberMistakes;
    }

    public void setNumberMistakes(int numberMistakes) {
        this.numberMistakes = numberMistakes;
    }

    public boolean[][] getCluesMatrix() {
        return cluesMatrix;
    }

    public void setCluesMatrix(boolean[][] cluesMatrix) {
        this.cluesMatrix = cluesMatrix;
    }

    public boolean[][] getDiscoveredMatrix() {
        return discoveredMatrix;
    }

    public void setDiscoveredMatrix(boolean[][] discoveredCellsMatrix) {
        this.discoveredMatrix = discoveredCellsMatrix;
    }

    private int[][] getNewBoard() {
        // navega por cada fila y va agregando los numero del 1 al 9 aleatoriamente
        // antes de agregar cada numero verifica si no existe en la fila, columna o zona
        // en caso de que si exista, se genera un nuevo nuero y se vuelve a comprobar hasta que no exista
        int[][] board = new int[9][9];
        List<Integer> asignedNumbersList = new ArrayList<>();
        List<Integer> invalidNumbersList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int newNumber = getNewRandomNumber(asignedNumbersList, invalidNumbersList);
                // verifica si no existe en la fila, columna o zona
                while (numberNotValid(i, j, newNumber, board)) {
                    // para que no se meta en bucle infinito, acumulamos los nros invalidos
                    invalidNumbersList.add(newNumber);
                    newNumber = getNewRandomNumber(asignedNumbersList, invalidNumbersList);
                }
                invalidNumbersList = new ArrayList<>();
                // se van guardando los numeros q fueron asignados
                asignedNumbersList.add(newNumber);
                board[i][j] = newNumber;
            }
            // por cada linea se limpia la lista de asignados
            asignedNumbersList = new ArrayList<>();
        }
        return board;
    }

    private boolean numberNotValid(int i, int j, int newNumber, int[][] board) {
        // si es -1 devolvemos false asi no se mete en bucle infinito
        if (newNumber == -1) {
            return false;
        }
        // devuelve true si el nro no puede ser asignado a esa coordenada porque ya existe en fila, columna o zona
        for (int each : listOneToNine) {
            int eachIndex = each - 1;
            if (eachIndex != j) {//verificamos que el nro no exista en la fila
                if (board[i][eachIndex] == newNumber) {
                    return true;
                }
            }
            if (eachIndex != i) {//verificamos que el nro no exista en la columna
                if (board[eachIndex][j] == newNumber) {
                    return true;
                }
            }
        }
        // verificamos que el nro no exista en la zona
        int iStartZoneIndex = (i / 3) * 3;
        int jStartZoneIndex = (j / 3) * 3;
        for (int x = iStartZoneIndex; x < iStartZoneIndex + 3; x++) {
            for (int y = jStartZoneIndex; y < jStartZoneIndex + 3; y++) {
                if (x != i || y != j) {
                    if (board[x][y] == newNumber) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getNewRandomNumber(List<Integer> asignedNumbersList, List<Integer> invalidNumbersList) {
        List<Integer> list = new ArrayList<>();
        list.addAll(listOneToNine);
        for (Integer each : asignedNumbersList) {
            list.remove(each);// sacamos todos los que ya estan asignados
        }
        for (Integer each : invalidNumbersList) {
            list.remove(each);// sacamos todos los que ya fueron invalidados
        }
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            return -1;
        }
        Random r = new Random();
        int randomIndex = r.nextInt(list.size());
        return list.get(randomIndex);
    }

    public int getNumberClues() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cluesMatrix[i][j]) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public String getDifficultyText(Context context) {
        // la dificultad se calcula en base a las casillas rellenas
        int clues = getNumberClues();
        String cluesText = " (" + clues + " " + context.getResources().getString(R.string.clues) + ")";
        switch (clues) {
            case CLUES_EASY:
                return context.getResources().getString(R.string.easy) + cluesText;
            case CLUES_MEDIUM:
                return context.getResources().getString(R.string.medium) + cluesText;
            case CLUES_HARD:
                return context.getResources().getString(R.string.hard) + cluesText;
            case CLUES_EXPERT:
                return context.getResources().getString(R.string.expert) + cluesText;
            default:
                return context.getResources().getString(R.string.custom) + cluesText;
        }
    }
}