import socketio
import json
import util.pi_lirc as pi

# IP = 'cheshire.cse.buffalo.edu'
# PORT = '2680'

IP = 'fathomless-brook-21291.herokuapp.com/'
# IP = 'indra-272100.appspot.com'

# IP = "localhost"
# PORT = "5000"

sio = socketio.Client()

@sio.event
def connect():
    sio.emit('message')
    print("Connection established!")


@sio.on('button_press',namespace='/pi')
def my_message(data):
    if type(data) == str:
        data = json.loads(data)

    print("Message received from server with ", data)
    print(data['remote'])
    res = pi.send_ir_signal(data['remote'], data['button'], method=data['method'])
    
    
@sio.on('search_request',namespace='/pi')
def handle_search_request(data):
    if type(data) == str:
        data = json.loads(data)
    search_results = pi.search(data['brand'], data['model'])
    print('search_request', data)
    print('search_results', search_results)
    response = {'results': search_results}
    if 'id' in data:
        response['id'] = data['id']
    print('response', response)
    sio.emit('search_results', response)


@sio.on('file_request',namespace='/pi')
def handle_file_request(data):
    output = {'success': False}
    if type(data) == str:
        data = json.loads(data)

    if 'id' in data:
        output['id'] = data['id']
        
    brand = data['brand']
    model = data['model']

    success, filename = pi.download_lirc_config(brand, model)

    if success:
        config = pi.read_lirc_config_file(filename)

        ### Grab name
        name = (config[config.find("name")+len("name"):config.rfind("bits")]).splitlines()[0].split()

        ### parse config into list of buttons
        buttons = {}
        start = 'begin codes'
        end = 'end codes'
        config = (config[config.find(start)+len(start):config.rfind(end)]).splitlines()

        for line in config:
            new_line = (line[:line.rfind('#')]).split()
            if new_line:
                buttons[new_line[0]] = new_line[1]

        resp = {
            "name": name,
            "buttons": buttons
        }


        output['file_contents'] = resp
        output['success'] = True

        print(output)

        # if file_contents is not None:
        #     output['success'] = True
        #     output['file_contents'] = file_contents

    sio.emit('file_response', json.dumps(output))




def main():
    # sio.connect("http://" + IP + ":" + PORT)
    sio.connect('https://' + IP, namespaces=['/pi'])
    sio.wait()


if __name__ == '__main__':
    main()
