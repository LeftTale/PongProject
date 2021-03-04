import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PongScreen extends JPanel implements KeyListener
{
    /*
    Simple game of pong, Made up of three classes, The graphics handler, the ball component and the paddle
     */
    JFrame frame = new JFrame();
    Font score = new Font("Score",1,100);

    Paddle paddle1;
    Paddle paddle2;
    Ball ball;

    boolean delay = false;

    int player1Score;
    int player2Score;
    int paddleHitCount;

    PongScreen()
    {
        frame.setSize(1280,720);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }
    void init()
    {
        frame.add(this);
        this.setBackground(Color.BLACK);
        frame.setVisible(true);
        frame.addKeyListener(this);

        paddle1 = new Paddle((int)getFrameX()/10,getFrameY()/2-50);
        paddle2 = new Paddle((getFrameX() - ( getFrameX()/10)), getFrameY()/2-50);
        ball = new Ball(getFrameX()/2,getFrameY()/2);

        new Thread(FrameTicker).start();
    }

    //Frame Sizes
    public double getFrameX() {return frame.getContentPane().getWidth();}
    public double getFrameY() {return frame.getContentPane().getHeight();}

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.fill(paddle1.getPaddleShape());
        paddle2.paddleXPos = getFrameX() - ( getFrameX()/10);
        g2d.fill(paddle2.getPaddleShape());
        g2d.fill(ball.getBallShape());

        g2d.setFont(score);
        g2d.drawString(String.valueOf(player1Score),(int)getFrameX()/5,(int)getFrameY()/5);
        g2d.drawString(String.valueOf(player2Score),(int)(getFrameX() - getFrameX()/5),(int)getFrameY()/5);
    }


    @Override
    public void keyPressed(KeyEvent e)
    {
        System.out.println("Lol");
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_S -> paddle1.setDirection(Direction.DOWN);
            case KeyEvent.VK_W -> paddle1.setDirection(Direction.UP);
            case KeyEvent.VK_R -> ball.setBallPos(getFrameX()/2,getFrameY()/2);
        }

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_DOWN -> paddle2.setDirection(Direction.DOWN);
            case KeyEvent.VK_UP -> paddle2.setDirection(Direction.UP);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_S:
            case KeyEvent.VK_W:
                paddle1.setDirection(Direction.STATIONARY);
                break;
        }

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_DOWN:

            case KeyEvent.VK_UP:
                paddle2.setDirection(Direction.STATIONARY);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private final Runnable FrameTicker = new Runnable()
    {
        /*
        Responsible for controlling the rate the game plays at and the order of operations
        at which it runs.
         */
        @SuppressWarnings("InfiniteLoopStatement")
        @Override
        public void run()
        {
            while(true)
            {
                repaint();
                paddle1.MovePaddle();
                paddle2.MovePaddle();

                ball.MoveBall();
                ball.DefineBall();

                try
                {
                    Thread.sleep(20);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    private final Runnable SmallDelay = new Runnable() {
        @Override
        public void run()
        {
            try
            {
                delay = true;
                Thread.sleep(100);
                delay = false;
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    };
    private final Runnable BallDelay = new Runnable()
    {
        /*
        Is called when the ball leaves the screen giving the player a slight delay.
         */
        @Override
        public void run()
        {
            ball.ballSpeed = 0;
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            ball.ballSpeed = 6;
        }
    };

    class Paddle
    {
        /*
        The paddle is a simple rectangle that can only move up and down
        within the constraints of the screen.
         */
        Shape paddleShape;
        Direction direction = Direction.STATIONARY;
        double paddleXPos;
        double paddleYPos;
        double paddleSpeed = 15;

        public Paddle(double xCoord, double yCoord)
        {
            this.paddleXPos = xCoord;
            this.paddleYPos = yCoord;
            DefineShape();
        }
        void MovePaddle()
        {

            switch (direction)
            {
                case UP:
                    if (!(paddleYPos <= 0))
                    {
                        paddleYPos = paddleYPos - paddleSpeed;
                    }
                    break;
                case DOWN:
                    if (!(paddleYPos >= getFrameY() - 100))
                    {
                        paddleYPos = paddleYPos + paddleSpeed;
                    }
                    break;
            }
            DefineShape();
        }
        void DefineShape(){paddleShape = new Rectangle2D.Double(paddleXPos,paddleYPos,20,100);}

        public Shape getPaddleShape() {
            return paddleShape;
        }
        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }

    class Ball
    {
        /*
        This class deals with the ball, It changes direction when it impacts with the two paddles or the top and bottom
        of the screen. If it goes the the left or right of the screen it appears again in the middle and waits for a
        short delay.
        */
        Shape ballShape;
        double ballPosX;
        double ballPosY;
        double ballSpeed = 6;
        double[] direction = new double[2];

        Ball(double xCoord, double yCoord)
        {
            this.ballPosX = xCoord;
            this.ballPosY = yCoord;
            direction[0] = 1;
            direction[1] = -1.0;
            DefineBall();
        }

        void DefineBall() {ballShape = new Ellipse2D.Double(ballPosX,ballPosY,30,30);}

        public Shape getBallShape() {return ballShape;}

        public void setBallPos(double ballPosX, double ballPosY)
        {
            this.ballPosX = ballPosX;
            this.ballPosY = ballPosY;
        }

        void MoveBall()
        {
            ballPosX = ballPosX + (direction[0] * ballSpeed);
            ballPosY = ballPosY + (direction[1] * ballSpeed);

            if((ballShape.intersects((Rectangle2D) paddle1.getPaddleShape()))
                    || (ballShape.intersects((Rectangle2D) paddle2.getPaddleShape())))
            {
                if (!delay)
                {
                    direction[0] *= -1;
                    direction[1] = (Math.random() * 2) - 1;
                    new Thread(SmallDelay).start();
                    paddleHitCount++;
                    if (paddleHitCount % 2 == 0)
                    {
                        ball.ballSpeed++;
                    }
                }
            }

            //Checks collision with the top or bottom of screen
            if(ballPosY <= 0)
            {
                if(!delay)
                {
                    direction[1] *= -1;
                    new Thread(SmallDelay).start();
                }
            }
            else if(ballPosY >= getFrameY()-40)
            {
                if(!delay)
                {
                    direction[1] *= -1;
                    new Thread(SmallDelay).start();
                }
            }

            //Checks collision with the left and right side of the screen
            if(ballPosX <= 0)
            {
                setBallPos(getFrameX()/2,getFrameY()/2);
                player2Score++;
                new Thread(BallDelay).start();
            }
            if(ballPosX >= getFrameX())
            {
                setBallPos(getFrameX()/2,getFrameY()/2);
                player1Score++;
                new Thread(BallDelay).start();
            }
        }
    }

    public static void main(String[] args)
    {
        new PongScreen().init();
    }




}
