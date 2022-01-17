package BluePlayer;

// Author: Michal Pasternak
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.awt.event.*;
import java.awt.event.KeyEvent;
// simple rover controller
class BluePlayer extends JPanel implements KeyListener {
    private char c = 'e';
    private static SocketCommunicator rover = new SocketCommunicator();
    private static SocketCommunicator ref = new SocketCommunicator();
    private static PlayerCommands drive;
    public static String IP = "127.0.0.1";
    private int oldKey = 0;

    public BluePlayer() {
        this.setPreferredSize(new Dimension(500, 500));
        addKeyListener(this);
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawString("Click to capture keyboard", 200, 250);
    }
    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      if (oldKey != key) {
        c = e.getKeyChar();

        if (c=='p'){
          System.out.println( "position: "+ drive.Send("player1,GPS()") );
        }
        
        if (c=='o'){
          System.out.println( "ball position: "+ drive.Send("ball,GPS()") );
        }
        if(c=='r'){
          System.out.println( "player1 suction strength: "+ drive.Send("player1,getSuction()") );
        }
        if(c=='k'){
          System.out.println( "Player One compass is: "+ drive.Send("player1,getCompass()") );
        }
        if (c=='a'){
          drive.SpinL();
        }
        if (c=='s'){
          drive.SpinR();
        }
        if (c=='w'){
          drive.Suck();
        }
        if (c=='e'){
          drive.Expel();
        }
        if (c=='d'){
          drive.SpinStop();
        }
        if (key == e.VK_LEFT) {
          drive.Left();
        }
        if (key == KeyEvent.VK_RIGHT) {
          drive.Right();
        }
        if (key == KeyEvent.VK_UP) {
          drive.Forward();
        }
        if (key == KeyEvent.VK_DOWN) {
          drive.Backward();
        }
        oldKey=key;
      }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {
      c = e.getKeyChar();
      int key = e.getKeyCode();

      if (key == KeyEvent.VK_LEFT) {
        drive.Stop();
      }

      if (key == KeyEvent.VK_RIGHT) {
        //System.out.println("sent right key");
        drive.Stop();
      }

      if (key == KeyEvent.VK_UP) {
        drive.Stop();
      }

      if (key == KeyEvent.VK_DOWN) {
        drive.Stop();
      }

      oldKey=0;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        c = e.getKeyChar();
        repaint();
        if (c=='q'){
          rover.close();
          System.exit(0);
        }
    }

    public static void main(String[] s) {
      JFrame f = new JFrame();
      int refPort = 0;
      int roverPort = 9001;
      String IP = "127.0.0.1";

      f.getContentPane().add(new BluePlayer());
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.setVisible(true);
      try {
        rover.connectToServer(IP,roverPort);
      }
      catch(java.io.IOException e){ e.printStackTrace();}
      drive = new PlayerCommands(rover,"player1");
    }

  /*  // file read
    public static java.util.List<String> readFile(String fileName) {
        int count = 1;
        File file = new File(fileName);
        // this gives you a 2-dimensional array of strings
        java.util.List<String> data = new ArrayList<>();
        Scanner inputStream;
        try {
            inputStream = new Scanner(file);

            while (inputStream.hasNext()) {
                data.add(inputStream.next());
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }*/
}
