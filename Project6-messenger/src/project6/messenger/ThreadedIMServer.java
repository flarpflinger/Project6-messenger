package project6.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreadedIMServer
    extends BasicServer implements Runnable {


  public ThreadedIMServer() {
    super(4220, 0);
  }

  public static void main(String[] args) {
    ThreadedIMServer myServer = new ThreadedIMServer();

    // spins on ServerSocket
    myServer.start();
  }

  protected void serviceConnection(Socket connection) throws IOException {
    ServerConnectionThread connectThread = new ServerConnectionThread(this,
        connection);
    connectThread.start();
  }

  public void run() {
    ServerConnectionThread thisThread = (ServerConnectionThread) Thread.
        currentThread();
    Socket thisSocket = thisThread.getSocket();
      try {
          String user = null;
          String password = null;
          BufferedReader in = new BufferedReader(new InputStreamReader(thisSocket.getInputStream()));
          PrintWriter out = new PrintWriter(thisSocket.getOutputStream(), true);
          out.println("1 flarpflinger password"); // "1 " + user + password);
          String input = null;
          while(true){
              input = in.readLine();
              int command = Integer.parseInt(input.substring(0, 1));
              
              switch(command){
                  case 3:
                      System.out.println("message recevied");
                      break;
                  case 4:
                      System.out.println("Buddy on");
                      break;
                  case 5:
                      System.out.println("Buddy off");
                      break;
                  case 6:
                      System.out.println("successful login");
                      break;
                  case 7:
                      System.out.println("Failed login");
                      break;
              }
          }
      } catch (IOException ex) {
          Logger.getLogger(ThreadedIMServer.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    
    
     /**********************
	a bunch of code deleted; this is where
	you handle the handshake with the client, and then
	put your readline busy wait
	**************************/
  }
  
}

