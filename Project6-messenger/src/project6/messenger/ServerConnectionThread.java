/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project6.messenger;

import java.net.Socket;

/**
 *
 * @author Nate H
 */

public class ServerConnectionThread extends Thread {
    
    private Socket mySocket;
    private ThreadedIMServer IMServer;
    
    public ServerConnectionThread(ThreadedIMServer t, Socket s) {
        mySocket = s;
        IMServer = t;
    }
    
    public Socket getSocket(){
        return mySocket;
    }
    
    
}
