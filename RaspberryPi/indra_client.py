import socketio
import json
import util.pi_lirc as pi

# IP = 'cheshire.cse.buffalo.edu'
#IP = 'fathomless-brook-21291.herokuapp.com/'

# PORT = '2680'
# PORT = '443'


# IP = '192.168.1.15'
# PORT = '8000'

IP = 'indra-272100.appspot.com'

sio = socketio.Client()


@sio.event
def connect():
    print("Connection established!")


@sio.on('button_press')
def my_message(data):
    if type(data) == str:
        data = json.loads(data)

    print("Message received from server with ", data)
    print(data['remote'])
    res = pi.send_ir_signal(data['remote'], data['button'])
    sio.emit('IRSEND Response', {'result': res})


@sio.on('search_request')
def handle_search_request(data):
    if type(data) == str:
        data = json.loads(data)
    search_results = pi.search(data['brand'], data['model'])
    print('search_request:', search_results)
    response = {'results': search_results, 'id': data['id']}
    sio.emit('search_results', response)


def main():
    # sio.connect("https://" + IP + ":" + PORT)
    sio.connect('https://' + IP)
    sio.wait()


if __name__ == '__main__':
    main()
