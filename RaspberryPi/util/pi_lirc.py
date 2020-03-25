from subprocess import check_output



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
