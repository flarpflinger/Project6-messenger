package project6.messenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreadedIMServer
    extends BasicServer implements Runnable {

    private ArrayList<Socket> activeUsers;
    private ArrayList<String> usernames;

  public ThreadedIMServer() {
    super(4220, 0);
    activeUsers = new ArrayList();
    usernames = new ArrayList();

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
          
          String input = null;
          boolean notLoggedIn = true;
          while(notLoggedIn){
            input = in.readLine();
            System.out.println(input);
            if(input.substring(0,1).equals("1")){

                try (BufferedReader br = new BufferedReader(new FileReader("users.txt")))
                {
                    String sCurrentLine;

                    int indexUsername = input.indexOf(" ", 2);
                    System.out.println(indexUsername + ".");
                    String inputUsername = input.substring(2, indexUsername);
                    String inputPassword = input.substring(indexUsername+ 1);
                    while ((sCurrentLine = br.readLine()) != null) {
                        int index = sCurrentLine.indexOf(" ");
                        String fileUsername = sCurrentLine.substring(0, index);
                        String filePassword = sCurrentLine.substring(index);
                        if(fileUsername.equals(inputUsername) && inputPassword.equals(filePassword)){
                            out.println("6 " + inputUsername);
                            notLoggedIn = false;
                            activeUsers.add(thisSocket);
                            usernames.add(inputUsername);
                            break;
                        }
                    }
                    if(notLoggedIn){
                        out.println("7 " + inputUsername);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } 

            }
          }
          
          while(true){
              input = in.readLine();
              int command = Integer.parseInt(input.substring(0, 1));
              System.out.println(input);
              switch(command){
                  case 0:
                      System.out.println("create login");
                      createLogin(input);
                      break;
                  case 2:
                      System.out.println("logoff");
                      String inputUsername = input.substring(2);
                      broadcast("5 " + inputUsername);
                      int index = usernames.indexOf(inputUsername);
                      activeUsers.remove(index);
                      usernames.remove(index);
                      break;
                  case 3:
                      System.out.println("message recevied");
                      int endSender = input.indexOf(' ', 2);
                      String senderUsername = input.substring(2, endSender);
                      String recptUsername = input.substring(endSender);
                      
                      int indexMessage = usernames.indexOf(recptUsername);
                      Socket s = activeUsers.remove(indexMessage);
                      usernames.remove(indexMessage);
                      //print to socket
                      //out.println(input);
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

    private void createLogin(String input) throws IOException {
        int indexUsername = input.indexOf(" ", 1);
        System.out.println(indexUsername + ".");
        String inputUsername = input.substring(1, indexUsername);
        String inputPassword = input.substring(indexUsername);
        
        File file = new File("/users/mkyong/filename.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
	BufferedWriter bw = new BufferedWriter(fw);
	bw.write(inputUsername + " " + inputPassword);
    }

    private void broadcast(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
}

