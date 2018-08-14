package com.example.divided.mathrush;

public class ScoreInformation {
    private String name;
    private String round;
    private String score;

    ScoreInformation(String name, String round, String score) {
        this.name = name;
        this.round = round;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getRound() {
        return round;
    }

    public String getScore() {
        return score;
    }

}
