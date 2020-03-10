import socketio

IP = 'localhost'
PORT = '5000'

sio = socketio.Client()

@sio.event
def connect():
    print("Connection established!")

@sio.event
def my_message(data):
    print("Message received from server with ", data)

@sio.event
def disconnect():
    print("Disconnected from server!")

sio.connect("http://" + IP + ":" + "PORT")
sio.wait()
