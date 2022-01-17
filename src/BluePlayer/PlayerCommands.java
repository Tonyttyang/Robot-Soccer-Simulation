package BluePlayer;

// Author: Michal Pasternak
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// format commands to unity string messages.
public class PlayerCommands {
  public static String name;
  private static SocketCommunicator rover;

  public PlayerCommands (SocketCommunicator sc,String n){
    rover = sc;
    name = n;
  }

  public static void Forward(){
    rover.noReply(name+",moveForward(100)");
    System.out.println(name+",moveForward(100)");
  }

  public static void Right(){
    rover.noReply(name+",moveRight(-100)");
    System.out.println(name+",moveRight(-100)");
  }

  public static void Left(){
    rover.noReply(name+",moveRight(100)");
    System.out.println(name+",moveRight(100)");
  }

  public static void Backward(){
    rover.noReply(name+",moveForward(-100)");
    System.out.println(name+",moveForward(-100)");
  }

  public static void SpinR(){
    rover.noReply(name+",spin(100)");
    System.out.println(name+",spin(100)");
  }

  public static void SpinL(){
    rover.noReply(name+",spin(-100)");
    System.out.println(name+",spin(-100)");
  }

  public static String Send(String msg) {
    System.out.println("sent: "+msg);
    return rover.send(msg);
  }

  public static void Suck(){
    rover.noReply(name+",setSuction(-100)");
    System.out.println(name+",setSuction(-100)");
  }

  public static void Expel(){
    rover.noReply(name+",setSuction(100)");
    System.out.println(name+",setSuction(100)");
  }

  public static void Stop() {
    rover.noReply(name+",stop()");
    System.out.println(name+",stop()");
  }
  public static void SpinStop() {
    rover.noReply(name+",spin(0)");
    System.out.println(name+",spin(0)");
  }
}
