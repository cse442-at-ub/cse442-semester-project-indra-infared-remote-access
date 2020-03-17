import socketio
import util.pi_lirc as pi

IP = 'localhost'
PORT = '5000'

sio = socketio.Client()

@sio.event
def connect():
    print("Connection established!")

@sio.event
def my_message(data):
    print("Message received from server with ", data)

@sio.on('search_request')
def disconnect(data):
    search_results = pi.lirc_search(data['brand'], data['model'])
    sio.emit('search_results', search_results)

sio.connect("http://" + IP + ":" + PORT)
sio.wait()
