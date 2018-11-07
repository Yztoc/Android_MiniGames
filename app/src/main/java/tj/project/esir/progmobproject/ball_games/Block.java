package tj.project.esir.progmobproject.ball_games;

public class Block {

    private int x;
    private int y;
    public static int width = 80;
    public static int height = 80;

    public Block(int x,int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
