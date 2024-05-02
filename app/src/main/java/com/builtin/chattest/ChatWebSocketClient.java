package com.builtin.chattest;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatWebSocketClient extends WebSocketClient {

    private ArrayList<String> smsR;
    private BlockingQueue<String> messageQueue;



    public ChatWebSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
        messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // Connection opened
        System.out.println("Connected to server");
    }


    @Override
    public void onMessage(String message) {
        // Message received
        System.out.println("Received message: " + message);

        //testing received data in array
        ArrayList<String>sms = new ArrayList<>();
        sms.add(message);
        System.out.println("my Arry.."+sms);

        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Connection closed
        System.out.println("Connection closed");
    }

    @Override
    public void onError(Exception ex) {
        // Error occurred
        ex.printStackTrace();
    }

    public void sendMessage(String message) {
        // Send message to server
        send(message);
    }
    // Method to receive messages and update UI

    public String getNextMessage() throws InterruptedException {
        // Retrieve and remove the next message from the queue
        return messageQueue.take();
    }

}
