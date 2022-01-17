package RedPlayer;

import BluePlayer.PlayerCommands;
import java.util.concurrent.TimeUnit;


// Author: Rohan Patel
public class PlayerController{
    private static SocketCommunicator redPlayer = new SocketCommunicator();
    private static String playerPosition="player2,GPS()";
    private static String ballPosition="ball,GPS()";
    public static String IP = "127.0.0.1";
    private static int roverPort = 9003;
    private static String blueplayerPosition="player1,GPS()";
    private int curr_angle=270;
    private boolean start = true;



    // Function that uses to calculate the distance between 2 points
    private static double getDis(float pointBx, float pointBz, float pointAx, float pointAz) {
    	double dis = Math.sqrt(Math.pow(pointBx-pointAx,2) + Math.pow(pointBz-pointAz,2));
        System.out.println("Distance:---"+dis);
        return dis;
    }
    
    // Function that uses to check the received player string and make sure that the string is what we want.
    private static String P_checkMsg() throws InterruptedException {
    	//get the player string
    	String player = redPlayer.send(playerPosition);
    	
        //check the player string length
        while (player.length() < 40) {
        	// sleep for 0.8s if the received string is "TimeOut"
        	if(player.contentEquals("TimeOut;player2;")) {
        		System.out.println("Time out- yes");
        		Thread.sleep(2500);
        	}
        	player = redPlayer.send(playerPosition);
        } // end while
        
        // print out the player string
        System.out.println("Player GPS: " + player);
        // return the player string
        return player;

    }
    
    // Function that uses to check the received ball string and make sure that the string is what we want.
    private String B_checkMsg() throws InterruptedException {
        //get the ball string
    	String Ball = redPlayer.send(ballPosition);
    	//check the ball string length
        while (Ball.length() < 32 || Ball.length()  >40) {
        	Ball = redPlayer.send(ballPosition);
        } // end while
        
        // print out the ball string
        System.out.println("Ball GPS: " + Ball);
        // return the ball string 
        return Ball;

    }

    // Function that uses to check if the player get the ball
    private boolean checkBall() throws InterruptedException {
        float rx; // x coordinate of the red player
        float rz; // z coordinate of the red player
        float bx; // x coordinate of the ball
        float bz; // z coordinate of the ball
        
        // get the player string 
        String R_player = P_checkMsg();
        // delay for 0.1 second
        Thread.sleep(100);
        // get the ball string 
        String Ball = B_checkMsg();
        
        // get the x and z coordinates of player and ball
        rx = getX(R_player);
        rz = getZ(R_player);
        bx = getX(Ball);
        bz = getZ(Ball);
        
        // calculate the distance
        double dis= getDis(rx,rz,bx,bz);
        // return boolean for the distance of 2.1
        return dis <= 2.1;
    }


    // Function that uses to spin a specific given angle
    private static void angleSpin(int angle) throws InterruptedException {
    	// get the compass string
        String compass = redPlayer.send("player2,getCompass()");
        
        // check the compass length 
        while (compass.length() >30 || compass.length()< 20) {
        	compass = redPlayer.send("player2,getCompass()");
        }
        
        // print out the compass
        System.out.println("Compass---"+ compass);
        
        // get the angle from the received compass string
        float cur_angle = Float.parseFloat(compass.split(",")[1].split(";")[0]);
        
        //spin until the compass reaches given angle
        while(!(cur_angle < (angle+10) & cur_angle > (angle-10))) {
        	// spin from right 
        	if(cur_angle <= angle) {
                spin(50);
                Thread.sleep(200);
                spin(0);
            }
        	// spin from left 
            else if(cur_angle > angle) {
                spin(-50);
                Thread.sleep(200);
                spin(0);
            }

            Thread.sleep(20);
            
            // check the compass string
            compass = redPlayer.send("player2,getCompass()");
            
            // print out the compass
            System.out.println("Compass---"+ compass);
         
            // check the compass length  
            while (compass.length() >30 || compass.length()< 20) {
            	compass = redPlayer.send("player2,getCompass()");
            }
            // get the angle from the received compass string
            cur_angle = Float.parseFloat(compass.split(",")[1].split(";")[0]);
        }// end while
    }// end angleSpin


    // controller for the red player
    private PlayerController() throws InterruptedException {
    	float rx; // x coordinate of the red player
        float rz; // z coordinate of the red player
        float bx; // x coordinate of the ball
        float bz; // z coordinate of the ball

        boolean red_start;
        
        try {
        	// while loop for the entire simulation
            while(start) {
                
            	// get the red player string
                String redplayer= P_checkMsg();
                
                // obtain the x and z coordinates of the red player
                float x_old =getX(redplayer);
                float z_old =getZ(redplayer);
                // boolean for spin
                boolean spin = false;
                
                // move the red player
                moveForward(100);
                Thread.sleep(50);
                // spin to the compass of 180
                angleSpin(180);
                
                Thread.sleep(100);
                // set the boolean to false
                red_start = false;
                
                // keep running until the red player gets the ball
                while(!red_start) {
                	// get the red player string
                    String R_player = P_checkMsg();
                    // get the ball string
                    String Ball = B_checkMsg();
                    
                    // obtain the x and z coordinates of the red player and ball
                    rx = getX(R_player);
                    rz = getZ(R_player);
                    bx = getX(Ball);
                    bz = getZ(Ball);
                    
                    // obtain the difference between new red player string and previous red player string
                    float dx = rx - x_old;
                    float dz = rz - z_old;
                    // update x and z coordinates of the new received string
                    x_old = rx;
                    z_old = rz;
                    
                    // check the difference  to determine if the red player is stuck 
                    if (dx ==0 && dz ==0) {
                    	System.out.print("Go Backward");
                        // check the x coordinate of the red player, move backwards and spin to the compass of 180 if x coordinate is greater than 0
						if(rx > 0) {
							moveForward(-100);
							Thread.sleep(1000);
							moveForward(0);
							angleSpin(180);
						 }
						 // check the x coordinate of the red player, move backwards and spin to the compass of 360 if x coordinate is less than or equal to 0
						 else {
							moveForward(-100);
							Thread.sleep(1000);
							moveForward(0);
							angleSpin(350);
						 }	  

                    }
                    // if red player is not stuck then use cross and dot products to get the ball
                    else {
                    	// cross product formula
                        float cross_3 = dx * (bz - rz) - dz * (bx - rx);
                        // determine the dot product
                        if (cross_3 == 0){
                            // dot product formula
                            float dot = rx * bx + rz * bz;
                            // determine the dot product
                            if (dot > 0) {
                                spin(0);
                                spin = false;
                            }
                            else if (dot < 0) {
                            	spin(75);
                            }
                        } 
                        else if (cross_3 > 0) {
                        	// spin left
                            spin(-50);
                            suction(-100);
                            spin = true;
                        } 
                        else if (cross_3 < 0) {
                            // spin right
                            spin(50);
                            suction(-100);
                            spin = true;
                        }
                        if (spin) {
                            Thread.sleep(200);
                            spin = false;
                            spin(0);
                        }

                        // move forward to suck the ball
                        moveForward(100);
                        suction(-100);
                        Thread.sleep(100);
                        moveForward(100);
                        suction(-100);
                        Thread.sleep(100);
                        // check if the red player gets the ball
                        red_start = checkBall();
                        
                        // double check and make sure the red player gets the ball
                        if (red_start){
                            Thread.sleep(100);
                             red_start = checkBall();
                            String player = P_checkMsg();
                            Thread.sleep(50);
                            String ball1 = B_checkMsg();
                            rx = getX(player);
                            rz = getZ(player);
                            bx = getX(ball1);
                            bz = getZ(ball1);
                            double dis = getDis(rx,rz,bx,bz);
                            red_start = (dis < 2.1);
                            System.out.println("Yes, red player got the ball");
                           
                        }
                    }

                }
                // move forwards and make the suction
                moveForward(100);
                suction(-100);
            
                // the red player has got the ball, move backwards and spin to the compass of 180
                moveForward(-100);
                Thread.sleep(200);
                moveForward(0);
                Thread.sleep(50);
                angleSpin(180);
                

                // obtain the distance between the red player and the net
                double dis = getDis(x_old,z_old,-40,0);
                // keep moving until the distance is greater than 20
                while(dis > 20 ) {
                	// get the red player string
                    String R_player = P_checkMsg();
                    // get the x and z coordinates of the red player
                    rx = getX(R_player);
                    rz = getZ(R_player);
                    // coordinates of the net
                    bx = -40;
                    bz = 0;
                    // get the difference of the red player string
                    float dx = rx-x_old;
                    float dz = rz-z_old;
                    // update to the new red player string
                    x_old = rx;
                    z_old = rz;
                    // check the difference  to determine if the red player is stuck 
                    if (dx == 0 || dz ==0) {
                    	System.out.print("Go Backward");
                    	// check the x coordinate of the red player, move backwards and spin to the compass of 180 if x coordinate is greater than 0
                    	if(rx > 0) {
                    		moveForward(-100);
							Thread.sleep(1000);
							moveForward(0);
							angleSpin(180);
						}
                    	// check the x coordinate of the red player, move backwards and spin to the compass of 360 if x coordinate is less than or equal to 0
						else {
							moveForward(-100);
							Thread.sleep(1000);
							moveForward(0);
							angleSpin(350);
						}	  
                    }
                    // if red player is not stuck then use cross and dot products to get the ball
                    else {
                    	// cross product formula
                        float cross_3 = dx * (bz - rz) - dz * (bx - rx);
                        // determine the cross product
                        if (cross_3 == 0){
                        	// dot product formula
                        	float dot = rx * bx + rz * bz;
                        	// determine the dot product
                        	if (dot > 0) {
                                spin(0);
                                spin = false;
                            } 
                            else if (dot < 0) {
                                 spin(80);
                            }
                        }
                        
                        else if (cross_3 > 0) {
                            // spin left
                            spin(-80);
                            spin = true;
                        } else if (cross_3 < 0) {
                            // spin right
                            spin(80);
                            spin = true;
                        }
                        if (spin) {
                            Thread.sleep(100);
                            spin = false;
                            spin(0);
                        }
                        // keep moving forwards
                        moveForward(100);
                        suction(-100);
                        Thread.sleep(100);
                        moveForward(100);
                        suction(-100);
                        Thread.sleep(100);
                    }
                    // obtain the red player string
                    String red = P_checkMsg();
                    // obtain x and z coordinates of the red player
                    rx=getX(red);
                    rz=getZ(red);
                    // calculate the distance 
                    dis = getDis(rx,rz,-40,0);
                    Thread.sleep(200);
                   
                }// end while
                // make the suction
                suction(60);
                // delay for 1 second
                Thread.sleep(1000);   
            }
            
        }catch(Exception e) {
        	new PlayerController();
        }
    }//end class








    public static void suction(int x){
        redPlayer.noReply("player2,setSuction("+String.valueOf(x)+")");
    }

    public static void moveForward(int x){
        redPlayer.noReply("player2,moveForward("+String.valueOf(x)+")");
    }

    public static void moveRight(int x){
        redPlayer.noReply("player2,moveRight("+String.valueOf(x)+")");
    }

    public static void spin(int x){
        redPlayer.noReply("player2,spin("+String.valueOf(x)+")");
    }

    public static void stop(){
        redPlayer.noReply("player2,stop()");
    }

    public static Float getX(String x){

        String[] splitted = x.split(",");

        return Float.parseFloat(splitted [splitted .length-2]);
    }

    public static Float getZ(String z){

        String[] splitted=z.split(",");
        return  Float.parseFloat(splitted[splitted.length-1].substring(0, splitted[splitted.length-1].length() - 1));

    }



//  public static void moveTo(int x,int z) throws InterruptedException{
//    boolean xFlag=false;
//    boolean zFlag=false;
//    Float xprev=0.0f;
//    Float zprev=0.0f;
//    String ret_mess = P_checkMsg();
//    //System.out.println(ret_mess);
//
//    Float xPos = getX(ret_mess);
//    Float zPos = getZ(ret_mess);
//
//    ////System.out.println(xPos);
//    ////System.out.println(zPos);
//    int xdir=1;
//    int zdir=1;
//    // //System.out.println("Moving x from "+Math.round(xPos)+" to "+x);
//    // //System.out.println("Moving z from "+Math.round(zPos)+" to "+z);
//    if(Math.round(xPos) < x){
//      xdir=-1;
//    }
//    if(Math.round(zPos) < z){
//      zdir=-1;
//    }
//    if(Math.round(xPos) == x || Math.abs(Math.round(xPos) - x)<2){
//      xdir=0;
//    }
//    if(Math.round(zPos) == z|| Math.abs(Math.round(zPos) - z)<2){
//      zdir=0;
//    }
//    moveForward(1000*xdir);
//    moveRight(100*zdir);
//    while(!xFlag || !zFlag){
//      if (Math.abs(xPos-x)<1 && !xFlag){
//        stop();
//        xFlag=true;
//      }
//      if (Math.abs(zPos-z)<1 && !zFlag){
//        stop();
//        zFlag=true;
//      }
//      if(xPos.equals(xprev) && Math.abs(xPos-x)>1){
//        stop();
//        moveForward(100*xdir);
//      }
//      if(zPos.equals(zprev) && Math.abs(zPos-z)>1){
//        stop();
//        moveRight(100*zdir);
//      }
//      try {
//          Thread.sleep(100);
//         } catch (InterruptedException e) {
//         // TODO Auto-generated catch block
//           e.printStackTrace();
//          }
//      ret_mess = P_checkMsg();
//      ////System.out.println("---2----");
//
//      xprev=xPos;
//      zprev=zPos;
//
//      xPos = getX(ret_mess);
//      zPos = getZ(ret_mess);
//
//     // //System.out.println(xPos);
//     // //System.out.println(zPos);
//
//    }
//  }

    public static void main(String[] args) throws InterruptedException {

        try {
            redPlayer.connectToServer(IP,roverPort);
            ////System.out.println("scored;player2;".substring(0,6));

        }
        catch(java.io.IOException e){ e.printStackTrace();}
        new PlayerController();


//    if(args[0].equals("medium")){
//      while(true){
//        moveTo(43,9);
//        moveTo(43,-9);
//      }
//    }
//    if(args[0].equals("easy")){
//      moveTo(45,1);
//    }
        //I'll do it once the compass is implemented
        // if(args[0].equals("hard")){
        // }
    }

}
