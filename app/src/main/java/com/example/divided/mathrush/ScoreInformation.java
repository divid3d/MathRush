package com.example.divided.mathrush;

public class ScoreInformation {
    private String name;
    private int round;
    private int score;

    ScoreInformation(String name, int round, int score) {
        this.name = name;
        this.round = round;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getRound() {
        return round;
    }

    public int getScore() {
        return score;
    }

}
