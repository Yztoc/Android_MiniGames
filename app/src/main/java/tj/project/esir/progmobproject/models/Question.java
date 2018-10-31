package tj.project.esir.progmobproject.models;

import android.util.Pair;

public class Question {

    private int id;
    private String title;
    private Pair<String,Integer> response1;
    private Pair<String,Integer> response2;
    private Pair<String,Integer> response3;

    public Question(){
        this.id = -1;
        this.title = "";
        this.response1 = new Pair<>("",0);
        this.response2 = new Pair<>("",0);
        this.response3 = new Pair<>("",0);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Pair<String, Integer> getResponse1() {
        return response1;
    }

    public void setResponse1(Pair<String, Integer> response1) {
        this.response1 = response1;
    }

    public Pair<String, Integer> getResponse2() {
        return response2;
    }

    public void setResponse2(Pair<String, Integer> response2) {
        this.response2 = response2;
    }

    public Pair<String, Integer> getResponse3() {
        return response3;
    }

    public void setResponse3(Pair<String, Integer> response3) {
        this.response3 = response3;
    }
}
