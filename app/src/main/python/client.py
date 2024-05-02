# Python code (client.py)

import threading
import websocket

def on_message(ws, message):
    print("Received message:", message)

def on_error(ws, error):
    print("Error:", error)

def on_close(ws):
    print("### closed ###")

def on_open(ws):
    def run():
        while True:
            message = input("Enter message: ")
            ws.send(message)
    threading.Thread(target=run).start()

def start_websocket(callback):
    ws = websocket.WebSocketApp("ws://localhost:8888/chat",
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)
    ws.on_open = on_open

    # Call the provided callback with the WebSocket instance
    callback.call(ws)

    # Run the WebSocket event loop
    ws.run_forever()
