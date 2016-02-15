package org.apache.kafka.connect.socket;

import org.apache.kafka.connect.errors.ConnectException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Andrea Patelli on 12/02/2016.
 */
public class SocketThread implements Runnable {
    public ConcurrentLinkedQueue<String> messages;

    private Integer port;

    private ServerSocket serverSocket;
    private BufferedReader input;
    private Socket clientSocket;

    public SocketThread(Integer port) {
        this.port = port;
        this.messages = new ConcurrentLinkedQueue<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ConnectException("Impossible to open socket on port " + port);
        }
    }

    @Override
    public void run() {
        try {
            if (clientSocket == null)
                connect();
            while (true) {
                String line;
                while ((line = input.readLine()) != null) {
                    messages.add(line);
                }
                connect();
            }
        } catch (Exception e) {
        }
    }

    private void connect() throws IOException {
        Socket newClient = serverSocket.accept();
        clientSocket = newClient;
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
