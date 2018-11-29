package tj.project.esir.progmobproject.models;

import java.io.Serializable;

public class Score implements Serializable {

    private int id;
    private String name_game;
    private int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_game() {
        return name_game;
    }

    public void setName_game(String name_game) {
        this.name_game = name_game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Score(int id, String name_game, int score){
        this.id=id;
        this.name_game = name_game;
        this.score = score;
    }

    public Score(){

    }



}
