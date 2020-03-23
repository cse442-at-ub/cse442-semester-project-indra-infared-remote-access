import socketio
import util.pi_lirc as pi

# IP = 'cheshire.cse.buffalo.edu'
IP = 'fathomless-brook-21291.herokuapp.com/'
# PORT = '2680'
PORT = '443'

sio = socketio.Client()


@sio.event
def connect():
    print("Connection established!")


@sio.on('button_press')
def my_message(data):
    print("Message received from server with ", data)
    res = send_ir_signal(data['remote'], data['button'])
    sio.emit('IRSEND Response', {'result': res})


@sio.on('search_request')
def handle_search_request(data):
    search_results = pi.search(data['brand'], data['model'])

    response = {'results': search_results, 'id': data['id']}
    sio.emit('search_results', response)


def main():
    sio.connect("http://" + IP + ":" + PORT, namespaces=['/pi'])
    sio.wait()


if __name__ == '__main__':
    main()
