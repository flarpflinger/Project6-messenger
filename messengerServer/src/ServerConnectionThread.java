/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.Socket;

/**
 *
 * @author Nate H
 */

public class ServerConnectionThread extends Thread {
    
    private Socket mySocket;
    
    public ServerConnectionThread(Runnable serverObject, Socket s) {
        super(serverObject);
        mySocket = s;
    }
    
    public Socket getSocket(){
        return mySocket;
    }
    
    
}
