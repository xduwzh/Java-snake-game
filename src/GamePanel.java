import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 700;
    static final int SCREEN_HEIGHT = 700;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    // head coordinate is the first
    final int x[] = new int[GAME_UNITS]; // Hold x coordinates of the body parts
    final int y[] = new int[GAME_UNITS]; // Hold y coordinates of the body parts
    int bodyParts = 6;
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // RLUD right left up down
    char lastDirection = 'R'; // in case input frequency too high and make 180 degree turn
    boolean running = false;
    Timer timer;
    Random random;


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if (running){
//            for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++){
//                g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0,i * UNIT_SIZE,SCREEN_WIDTH, i * UNIT_SIZE);
//            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++){
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            // draw score
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+appleEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }
    public void newApple() {
        boolean valid;
        do {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            valid = true;
            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i]) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
    }


    public void move(){
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
        lastDirection = direction;
    }
    public void checkApple(){
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }
    public void checkCollisions(){
        // checks if head collides with body
        for (int i = bodyParts; i>0; i--){
            if ((x[0]== x[i]) && (y[0] == y[i])){
                running = false;
            }
        }

        // check if head collides with walls
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }


        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

//        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: "+appleEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: "+appleEaten))/2, g.getFont().getSize());
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (running){
                switch (e.getKeyCode()){
                    case KeyEvent.VK_LEFT:
                        if(lastDirection != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(lastDirection != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(lastDirection != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if(lastDirection != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
            else {
                resetGame();
            }
        }

    }

    public void resetGame() {
        direction = 'R';
        lastDirection = 'R';
        bodyParts = 6;
        appleEaten = 0;
        running = true;


        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }


        newApple();

        timer.restart();
    }

}
