package tj.project.esir.progmobproject.ball_games;

public class Block {

    private int x;
    private int y;
    private int width = 40;
    private int height = 40;

    public Block(int x,int y,int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
