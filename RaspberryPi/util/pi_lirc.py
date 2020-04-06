from subprocess import check_output
import shutil
import dbus
import time
import os


LIRC_CONF_DIR = '/etc/lirc/lircd.conf.d'



def send_ir_signal(remote_name:str, button:str, device:str=None) -> bool:
    """Sends an IR signal with LIRC.

    Attempts to use LIRC to send the IR command associated with the provided remote_name and button.
    If no device is provided then the default LIRC device will be used. 

    Parameters
    ----------

    remote_name : str
        Name of the remote config to use.

    button : str
        Name of the button to 'press'

    device : str, optional
        The device that LIRC will use to send the command. If None then the default device is used. 
        It is highly recommended that you always call this with the LIRC device that is used 
        for sending commands as the configured default may not be correct. (default is None)

    Returns
    -------
    bool
        True if successful, False if not.
    """
    command = []

    if not device:
        command = ['irsend', 'SEND_ONCE', remote_name, button]

    output = None
    try:
        output = check_output(command)
    except:
        return False

    return len(output) == 0

  
def search(brand, device):
    output = check_output(['irdb-get','find', brand]).decode()
    output = output.split("\n")
    
    op1 = [i1.split('.l', 1)[0] for i1 in output[0:-1]]
    op2 = [i2.split('/', 1) for i2 in op1]                
    res1 = [i[0] for i in op2]
    res2 = [i[1] for i in op2]
    final = [{'brand': f, 'device': c} for f,c in zip(res1,res2)]           
    result = list(filter(lambda item: device.lower() in item['device'].lower(), final))    
    return result 


def download_lirc_config(brand: str, device: str, dst_dir=LIRC_CONF_DIR ) -> (bool, str):
    
    lookup = brand + '/' + device + '.lircd.conf'
    output = check_output(['irdb-get', 'download', lookup]).decode()

    if 'Cannot' in output:
        return (False, None)
    
    filename = output.split('as')[-1].strip()

    try:
        resulting_location = shutil.move('./' + filename, dst_dir)
        pass
    except shutil.Error:
        return (True, filename)
        pass

    output = (False, None)
    if dst_dir in resulting_location and restart_lirc_service():
        output = (True, filename)
    
    return output


def restart_lirc_service():
    """
    Requires authorization to interact with systemd. Therefore, run your piece of code with 'sudo' privileges.
    """

    sysbus = dbus.SystemBus()
    systemd = sysbus.get_object('org.freedesktop.systemd1', '/org/freedesktop/systemd1')
    manager = dbus.Interface(systemd, 'org.freedesktop.systemd1.Manager')

    output = manager.RestartUnit('lircd.service', 'fail')

    time.sleep(.5)

    return is_lirc_running(sysbus)


def is_lirc_running(sysbus=dbus.SystemBus()):
    """
    Requires authorization to interact with systemd. Therefore, run your piece of code with 'sudo' privileges.
    """

    systemd = sysbus.get_object('org.freedesktop.systemd1', '/org/freedesktop/systemd1')
    manager = dbus.Interface(systemd, 'org.freedesktop.systemd1.Manager')

    service = sysbus.get_object('org.freedesktop.systemd1', object_path=manager.GetUnit('lircd.service'))
    interface = dbus.Interface(service, dbus_interface='org.freedesktop.DBus.Properties')

    return interface.Get('org.freedesktop.systemd1.Unit', 'ActiveState') == 'active'


def read_lirc_config_file(filename, src_dir=LIRC_CONF_DIR):
    path_to_file = src_dir + '/' + filename
    output = None

    if os.path.exists(path_to_file):
        with open(path_to_file, 'r') as lirc_f:
            try:
                output = lirc_f.read()              
                pass
            except:
                output = None
                pass
            finally:
                lirc_f.close()

    return output
        