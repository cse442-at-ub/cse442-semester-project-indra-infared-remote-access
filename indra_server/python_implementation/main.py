import eventlet
eventlet.monkey_patch()

from flask import Flask, request
from flask_socketio import SocketIO, emit

app = Flask(__name__)
socketio = SocketIO(app)


@socketio.on('connect')
def connect():
    print("Connection")


@socketio.on('button_press')
def press(data):
    print('button_press')
    emit('button_press', data, broadcast=True)


@socketio.on('search_request')
def search_request(data):
    request_data = {'remote': data['remote'], 'button': data['button'], 'id': request.id}
    emit('search_request', request_data, broadcast=True)


@socketio.on('search_results')
def search_results(data):
    search_results = data['results']
    recipient_id = data['id']

    emit('search_results', search_results, room=recipient_id)


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8080, debug=True)