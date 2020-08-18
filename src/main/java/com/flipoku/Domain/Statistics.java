package com.flipoku.Domain;

import android.content.Context;

import com.flipoku.Repository.RepositoryEntity;
import com.flipoku.Repository.StatisticsRepository;

public class Statistics extends RepositoryEntity {
    /*Repository Static Structure*/
    private static final long serialVersionUID = 1L;
    public static Statistics clone(Statistics obj) {
        try {
            return (Statistics) obj.clone();
        } catch (CloneNotSupportedException e) {/*not possible*/}
        return null;
    }

    public static StatisticsRepository getRepository(Context context) {
        return new StatisticsRepository(context);
    }
    /*Repository Static Structure*/

    private int clues;// dificultad, osea cantidad de pistas al inicio del juego
    private long bestTime;// valor, osea el tiempo en milis
    private int wins;
    private int loses;
    private int streak;

    public Statistics(int clues, long bestTime, int wins, int loses, int streak) {
        this.clues = clues;
        this.bestTime = bestTime;
        this.wins = wins;
        this.loses = loses;
        this.streak = streak;
    }

    public int getClues() {
        return clues;
    }

    public void setClues(int clues) {
        this.clues = clues;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
