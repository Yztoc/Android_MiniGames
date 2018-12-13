package tj.project.esir.progmobproject.multiplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;

public class MultiplayParameters implements Serializable {

    private int level;
    private List<Question> listQuestion;
    private List<CustomPair<Integer,Integer>> listCalculs;
    private String connectionType;



    public MultiplayParameters(int level, List<Question> listQuestion, List<CustomPair<Integer,Integer>> listCalculs, String connectionType){
        this.level = level;
        this.listCalculs = listCalculs;
        this.listQuestion = listQuestion;
        this.connectionType = connectionType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Question> getListQuestion() {
        return listQuestion;
    }

    public void setListQuestion(List<Question> listQuestion) {
        this.listQuestion = listQuestion;
    }

    public List<CustomPair<Integer, Integer>> getListCalculs() {
        return listCalculs;
    }

    public void setListCalculs(List<CustomPair<Integer, Integer>> listCalculs) {
        this.listCalculs = listCalculs;
    }


    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }


    public void addQuestion(Question question){
        this.listQuestion.add(question);
    }

    public void addCalcul(CustomPair<Integer,Integer> calcul){
        this.listCalculs.add(calcul);
    }

    public MultiplayParameters(){
        this.level = 1;
        this.listCalculs = new ArrayList<CustomPair<Integer, Integer>>();
        this.listQuestion = new ArrayList<>();
        this.connectionType = "none";
    };


    @Override
    public String toString() {
        return "MultiplayParameters{" +
                "level=" + level +
                ", listQuestion=" + listQuestion +
                ", listCalculs=" + listCalculs +
                '}';
    }
}