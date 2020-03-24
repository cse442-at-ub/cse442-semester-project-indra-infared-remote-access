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
    if data['type'] == 'IRSEND':
        res = send_ir_signal(data['remote_name'], data['button'])
        sio.emit('IRSEND Response', {'result': res})


@sio.on('search_request')
def handle_search_request(data):
    search_results = pi.search(data['brand'], data['model'])

    response = {'results': search_results, 'id': data['id']}
    sio.emit('search_results', response)


def main():
    sio.connect("http://" + IP + ":" + "PORT")
    sio.wait()


if __name__ == '__main__':
    main()
