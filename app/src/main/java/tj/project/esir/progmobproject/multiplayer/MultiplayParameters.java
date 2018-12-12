package tj.project.esir.progmobproject.multiplayer;

import java.io.Serializable;
import java.util.List;

import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;

public class MultiplayParameters implements Serializable {

    private int level;
    private List<Question> listQuestion;
    private List<CustomPair<Integer,Integer>> listCalculs;

    public MultiplayParameters(int level, List<Question> listQuestion, List<CustomPair<Integer,Integer>> listCalculs){
        this.level = level;
        this.listCalculs = listCalculs;
        this.listQuestion = listQuestion;
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

    public MultiplayParameters(){};



}
