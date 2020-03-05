package com.shpp.p2p.cs.ykohuch.assignment4;

import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;


import java.awt.*;
import java.awt.event.MouseEvent;

/**classic arcade game Breakout*/
public class Breakout extends WindowProgram {
    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Ball velocity */
    private static final double BALL_VELOCITY_Y = +3.0;

    /** Width of a brick */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Animation delay or pause time between ball moves */
    private static final int DELAY = 10;

    /** Number of turns */
    private static final int NTURNS = 3;

    /** which is used to display the try number stored in a variable tries*/
    private static GLabel OTPUT_ATTEMPTS;

    /*basic method*/
    public void run() {
        //create blocks and display them
        makeBricks(getWidth() / 2, BRICK_Y_OFFSET);
        //display paddle on the screen
        add(theBoard());
        // paddle movement physics
        addMouseListeners();

        for(int i=0; i < NTURNS; i++) {
            //display ball on the screen
            GOval ball = makeBall();
            add(ball);
            printNumberOfAttempts();
            //ball motion physics
            getBallVelocity();
                //a method in which the written motion of the ball
                moveBall(ball);
                // ball collision processing
                getCollidingObject(ball);
                if (counter == 0) {
                    ball.setVisible(false);
                    congratulations();
                    break;
                }
                if (counter > 0) {
                    remove(ball);
                    remove(OTPUT_ATTEMPTS);
                }
        }
        if (counter > 0) {
            gameOver();
        }
    }

    //here method create a paddle from which the ball will be reflected
    private GObject theBoard() {
        GRect theBoard = new GRect(WIDTH/2-PADDLE_WIDTH/2, HEIGHT-(PADDLE_Y_OFFSET+PADDLE_HEIGHT), PADDLE_WIDTH, PADDLE_HEIGHT);
        theBoard.setFillColor(Color.BLACK);
        theBoard.setFilled(true);
        this.addMouseListener(this);
        return (theBoard);
    }

    // a global variable for the mouseMoved method
    private GObject movedPaddle = theBoard();
    //number of tries for output at the screen
    private int tries = 1;

    //method that binds the mouse to the theBoard object
    @Override
    public void mouseMoved(MouseEvent e) {
        movedPaddle = getElementAt(movedPaddle.getX(), movedPaddle.getY());
            double newX = e.getX() - movedPaddle.getWidth() - PADDLE_WIDTH/ 2.0;
            double newY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
            movedPaddle.setLocation(newX, newY);
            // restrictions on the movement of the paddle that it did not go beyond GCanvas
            if (e.getX() < PADDLE_WIDTH / 2.0) {
               newX = PADDLE_WIDTH / 2.0;
            } else if (e.getX() > (APPLICATION_WIDTH - PADDLE_WIDTH / 2.0)) {
                newX = APPLICATION_WIDTH - PADDLE_WIDTH / 2.0;
            }
            else {
                newX = e.getX();
            }
            this.movedPaddle.setLocation((newX - PADDLE_WIDTH / 2.0), newY);
    }

    /*method for create a ball*/
    private GOval makeBall(){
        GOval ball = new GOval(WIDTH/2-BALL_RADIUS/2, HEIGHT/2-BALL_RADIUS/2, BALL_RADIUS*2, BALL_RADIUS*2);
        ball.setFillColor(Color.BLACK);
        ball.setFilled(true);
        return (ball);
    }

        //global variables for ball physics
    private double vx;
    private double vy;

    //ball motion physics
    private void getBallVelocity() {
        RandomGenerator rgen = RandomGenerator.getInstance();
        vy = BALL_VELOCITY_Y;
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
    }

        //a method in which the written motion of the ball
    private void moveBall(GOval ball) {
        waitForClick();
       while(counter > 0) {
           ball.move(vx, vy);
           // restrictions on the movement of the ball that it did not go beyond screen
           if ((ball.getX() - vx <= 0 && vx < 0 )|| (ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx>0)) {
               vx = -vx;
           }
           /*check the top wall
            *don't need to check for the bottom wall, since the ball can fall through the wall at that point*/
           if ((ball.getY() - vy <= 0 && vy < 0 )) {
               vy = -vy;
           }
            GObject collider = getCollidingObject(ball);
           //here is the functionality of a collision ball with a paddle
           if (collider == movedPaddle && vy > 0) {
               /*here ball bounces from the top side of paddle*/
               if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2 ||
                       ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2) {
                   vy = -vy;
               }
               /*protection from being stuck in the board if it hits the side */
               if (ball.getY() + BALL_RADIUS * 2 > movedPaddle.getY()) {
                   remove(ball);
                   tries ++;
                   break;
               }
           }
           /*if ball will contact with label, he will not bounce*/
           else if(collider == OTPUT_ATTEMPTS){
               ball.move(vx,vy);
           }
           // ball hit a brick from below
           else if (collider != null) {
               vy = -vy;
               counter--;
               remove(collider);
           }
           pause(DELAY);
           if (ball.getY() >= getHeight()) {
               tries ++;
               break;
           }
           if(counter == 0) {
               break;
           }
       }
    }

    //ball collision processing an find contact with other elements in screen
    private GObject getCollidingObject(GObject ball) {
            if ((getElementAt(ball.getX(), ball.getY())) != null) {
                return getElementAt(ball.getX(), ball.getY());
            }
            if (getElementAt((ball.getX() + BALL_RADIUS * 2), ball.getY()) != null) {
                return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
            }
            if (getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null) {
                return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
            }
            if (getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2)) != null) {
                return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
            }
        //need to return null if there are no objects present
        else{
            return null;
        }
    }

    //counter which contains total number of bricks
    private int counter = NBRICKS_PER_ROW * NBRICK_ROWS;

    /*the method creates lines of blocks and paints them into the required colors*/
    private void makeBricks(double dx, double dy) {
        for( int row = 0; row < NBRICK_ROWS; row++ ) {
            for (int column = 0; column < NBRICKS_PER_ROW; column++) {
                //set location for bricks
                 double x = dx - (NBRICKS_PER_ROW*BRICK_WIDTH)/2 -
                         ((NBRICKS_PER_ROW-1)*BRICK_SEP)/2 + column*BRICK_WIDTH + column*BRICK_SEP;
                 double y = dy + row*BRICK_HEIGHT + row*BRICK_SEP;
                GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                add (brick);
                brick.setFilled(true);
                //set colour for different rows of bricks
                if (row < 2) {
                    brick.setColor(Color.RED);
                } else if (row == 2 || row == 3) {
                    brick.setColor(Color.ORANGE);
                } else if (row == 4 || row == 5) {
                    brick.setColor(Color.YELLOW);
                } else if (row == 6 || row == 7) {
                    brick.setColor(Color.GREEN);
                } else {
                    brick.setColor(Color.CYAN);
                }
            }
        }
    }

    /*method which used to draw Glabel with attempts*/
    private void printNumberOfAttempts() {
        OTPUT_ATTEMPTS = new GLabel("Attempt number: " + tries);
        OTPUT_ATTEMPTS.setFont("Sans-20");
        OTPUT_ATTEMPTS.setColor(Color.black);
        add(OTPUT_ATTEMPTS, getWidth()/2, OTPUT_ATTEMPTS.getAscent());
    }

   /* method displays greetings to the winner*/
    private void congratulations() {
        setBackground(Color.YELLOW);
        GLabel Winner = new GLabel ("Winner!!!", getWidth()/2, getHeight()/2);
        Winner.move(-Winner.getWidth()*2, -Winner.getHeight());
        Winner.setFont("Sans-40");
        Winner.setColor(Color.BLUE);
        add (Winner);
    }

   /* the method outputs a message about the loss*/
    private void gameOver() {
        setBackground(Color.BLUE);
        GLabel gameOver = new GLabel ("Game Over", getWidth()/2, getHeight()/2);
        gameOver.setFont("Sans-40");
        gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
        gameOver.setColor(Color.WHITE);
        add (gameOver);
    }
}



