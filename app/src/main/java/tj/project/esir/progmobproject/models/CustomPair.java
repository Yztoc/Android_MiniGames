package tj.project.esir.progmobproject.models;

import java.io.Serializable;

public class CustomPair<F,S> implements Serializable {
    private F first;
    private S second;

    public CustomPair(F first, S second){
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "CustomPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
