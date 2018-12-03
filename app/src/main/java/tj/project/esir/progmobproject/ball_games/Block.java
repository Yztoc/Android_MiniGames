package tj.project.esir.progmobproject.ball_games;

public class Block {

    private int x;
    private int y;
    public static int width = 100;
    public static int height = 100;



    public Block(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
