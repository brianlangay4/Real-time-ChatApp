package com.builtin.chattest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private ChatWebSocketClient webSocketClient;
    private List<String> smsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, smsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        try {
            webSocketClient = new ChatWebSocketClient("ws://192.168.91.1:8888/chat");
            //webSocketClient = new ChatWebSocketClient("ws://127.0.0.1:8888/chat");
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // UI components
        EditText messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);
        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                webSocketClient.sendMessage(message);
                // Clear the edit text after sending
                messageEditText.getText().clear();
            }
        });
        // Start listening for incoming messages
        listenForMessages();
    }
    private void listenForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = webSocketClient.getNextMessage();
                    smsList.add(message);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close WebSocket connection when the activity is destroyed
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //adapter.getItem(position)
    }
}
