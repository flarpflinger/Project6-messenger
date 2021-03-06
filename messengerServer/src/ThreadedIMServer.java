

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
//          boolean notLoggedIn = true;
//          while(notLoggedIn){
//            input = in.readLine();
//            System.out.println(input);
//            if(input.substring(0,1).equals("1")){
//
//                try (BufferedReader br = new BufferedReader(new FileReader("users.txt")))
//                {
//                    String sCurrentLine;
//
//                    int indexUsername = input.indexOf(" ", 2);
//                    //System.out.println(indexUsername + ".");
//                    String inputUsername = input.substring(2, indexUsername);
//                    String inputPassword = input.substring(indexUsername+ 1);
//                    System.out.println("input: " + inputUsername+"."+inputPassword+".");
//                    while ((sCurrentLine = br.readLine()) != null) {
//                        int index = sCurrentLine.indexOf(" ");
//                        String fileUsername = sCurrentLine.substring(0, index);
//                        String filePassword = sCurrentLine.substring(index + 1);
//                        System.out.println(fileUsername + "."+filePassword+".");
//                        if(fileUsername.equals(inputUsername) && inputPassword.equals(filePassword)){
//                            System.out.println("out: 6 "+ inputUsername);
//                            out.println("6 " + inputUsername);
//                            broadcast("4 "+ inputUsername);
//                            broadcastBuddies("4 " + inputUsername, thisSocket);
//                            notLoggedIn = false;
//                            activeUsers.add(thisSocket);
//                            usernames.add(inputUsername);
//                            break;
//                        }
//                    }
//                    if(notLoggedIn){
//                        System.out.println("out: 7 " + inputUsername);
//                        out.println("7 " + inputUsername);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } 
//
//            }
//          }
          
          while(true){
              input = in.readLine();
              System.out.println("input:"+input);
                int command = Integer.parseInt(input.substring(0, 1));
                System.out.println(input);
                switch(command){
                    case 0:
                        System.out.println("create login");
                        createLogin(input);
                        loginUser(input, thisSocket, out);
                        break;
                    case 1:
                        loginUser(input, thisSocket, out);
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
                        int endRecpt = input.indexOf(' ', endSender + 1);
                        String recptUsername = input.substring(endSender + 1, endRecpt);
                        System.out.println(recptUsername);
                        int indexMessage = usernames.indexOf(recptUsername);
                        Socket s = activeUsers.get(indexMessage);
                        try {
                          PrintWriter sOut = new PrintWriter(s.getOutputStream(), true);
                          sOut.println(input);
                        } catch (IOException ex) {
                            //Logger.getLogger(MessengerMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
        int indexUsername = input.indexOf(" ", 2);
        System.out.println(indexUsername + ".");
        String inputUsername = input.substring(2, indexUsername);
        String inputPassword = input.substring(indexUsername+1);
        
        File file = new File("users.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
	BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
	bw.write(inputUsername + " " + inputPassword);
        bw.close();
    }

    private void broadcast(String string) {
        for(Socket currSocket : activeUsers){
            try {
                PrintWriter sOut = new PrintWriter(currSocket.getOutputStream(), true);
                sOut.println(string);
                System.out.println("Broadcast: " + string);
            } catch (IOException ex) {
                //Logger.getLogger(MessengerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void broadcastBuddies(String string, Socket s) {
        try {
            PrintWriter sOut = new PrintWriter(s.getOutputStream(), true);
            
            
            for(String currUsername : usernames){
                sOut.println("4 " + currUsername);
                System.out.println("BroadcastBuddies: " + "4 "+currUsername);
                
            }
        }catch (IOException ex) {
            //Logger.getLogger(MessengerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loginUser(String input, Socket thisSocket, PrintWriter out) {
       boolean notLoggedIn = true;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt")))
            {
                String sCurrentLine;

                int indexUsername = input.indexOf(" ", 2);
                //System.out.println(indexUsername + ".");
                String inputUsername = input.substring(2, indexUsername);
                String inputPassword = input.substring(indexUsername+ 1);
                System.out.println("input: " + inputUsername+"."+inputPassword+".");
                while ((sCurrentLine = br.readLine()) != null) {
                    int index = sCurrentLine.indexOf(" ");
                    String fileUsername = sCurrentLine.substring(0, index);
                    String filePassword = sCurrentLine.substring(index + 1);
                    System.out.println(fileUsername + "."+filePassword+".");
                    if(fileUsername.equals(inputUsername) && inputPassword.equals(filePassword)){
                        System.out.println("out: 6 "+ inputUsername);
                        out.println("6 " + inputUsername);
                        broadcast("4 "+ inputUsername);
                        broadcastBuddies("4 " + inputUsername, thisSocket);
                        notLoggedIn = false;
                        activeUsers.add(thisSocket);
                        usernames.add(inputUsername);
                        break;
                    }
                }
                if(notLoggedIn){
                    System.out.println("out: 7 " + inputUsername);
                    out.println("7 " + inputUsername);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } 
    }
  
}

