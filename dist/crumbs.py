from PIL import Image, ImageTk
import easygui
import commands
import subprocess as p
import platform 
import os
import re
import sys
import atexit
from time import sleep
import signal
import Tkinter as tk

IMAGE_PATH = "LoadingPageDesign.ppm"

class Splash(object):
    "Splash Screen GUI"
    def __init__(self, root):
        self.root = root
        self.root.overrideredirect(True)
        w = self.root.winfo_screenwidth()
        h = self.root.winfo_screenheight()
        # Full screen
        # Display an image
        self.label = tk.Label(self.root)
        self.label._image = tk.PhotoImage(file=IMAGE_PATH)
        self.label.configure(image = self.label._image)
        self.label.pack()
        self.root.after(10000, self.root.quit)

def center(win):
    win.update_idletasks()
    width = win.winfo_width()
    frm_width = win.winfo_rootx() - win.winfo_x()
    win_width = width + 2 * frm_width
    height = win.winfo_height()
    titlebar_height = win.winfo_rooty() - win.winfo_y()
    win_height = height + titlebar_height + frm_width
    x = win.winfo_screenwidth() // 2 - win_width // 2
    y = win.winfo_screenheight() // 2 - win_height // 2
    win.geometry('{}x{}+{}+{}'.format(width, height, x, y))
    win.deiconify()

def inplace_change(filename, old_regex, new_string):
    # Safely read the input filename using 'with'
    with open(filename) as f:
        s = f.read()

    # Safely write the changed content, if found in the file
    with open(filename, 'w') as f:
        s = re.sub(old_regex, new_string, s)
        f.write(s)

try:
    predictorPid = int(p.check_output(['pidof','-s','nn_crumbs']))
    os.kill(predictorPid, signal.SIGTERM)
except:
    a=0

configPath = os.path.join('config', 'ethereumj.conf')

hostSystem = platform.system()

#retrieve private ips
ip = "127.0.0.1"
addresses = []
if hostSystem == 'Linux':
    ips = p.check_output(['hostname', '-I'])
    addresses = ips.split()
elif hostSystem == 'Darwin':
    for NUMBER in [0,5]:
        addresses.push(commands.getoutput('ipconfig getifaddr en' + str(NUMBER)))
elif hostSystem == 'Windows':
    ips = commands.getoutput("ipconfig | where {$_ -match 'IPv4.+\s(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})' } | out-null; $Matches[1]")
    addresses = ips.split()

#selection of bind ip
for address in addresses:
    print(address)
if len(addresses) == 0:
    ip = easygui.enterbox(msg='Unable to detect your IP address, please enter the desired IP address to use.\nIf remained at 127.0.0.1, the application will not be able to communicate across computers', title='No IP detected', default='127.0.0.1', strip=True)
    if not re.match(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$', peer):
        easygui.msgbox('Invalid IP address entered')
        sys.exit(0)
elif len(addresses) != 1:
    ip = easygui.choicebox(msg='Multiple IP addresses detected, please select IP address to be used', title='Select your IP', choices=addresses)
else:
    ip = addresses[0]

#enter ip of peers
if ip != '127.0.0.1':
    peer = easygui.enterbox(msg='Your ip address is : ' + ip + '\nplease enter a peer ip address', title='IP Resolution', strip=True)
    if peer:
        print(peer)
if not re.match(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$', peer):
    easygui.msgbox('Invalid IP address entered')
    sys.exit(0)
inplace_change(configPath, r'ip_addr = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', 'ip_addr = ' + ip)
inplace_change(configPath, r'peer_ip = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', 'peer_ip = ' + peer)

#run services
backendLog = open('backend.log','w+')
predictorLog = open('predictor.log','w+')

backend = p.Popen(['java','-jar','ethereum-1.0-SNAPSHOT.jar'], stdout=backendLog)

if hostSystem == 'Linux':
    predictor = p.Popen(['components-linux/predictor/nn_crumbs'], stdout=predictorLog)


print('backend pid: ' + str(backend.pid))
print('predictor pid: ' + str(predictor.pid))

root = tk.Tk()

appl = Splash(root)
center(root)
root.mainloop()

sleep(9)

root.destroy()

if hostSystem == 'Linux':
    frontend = p.Popen(['components-linux/frontend/crumbs'])
elif hostSystem == 'Darwin':
    frontend = p.Popen(['components-linux/frontend/crumbs.app/Contents/MacOS/crumbs'])

print('frontend pid: ' + str(frontend.pid))

def exitHandler():
    backend.kill()
    predictor.kill()
#    p.call(['kill',int(p.check_output(['pidof','-s','nn_crumbs']))])
    print(int(p.check_output(['pidof','-s','nn_crumbs'])))
    predictorPid = int(p.check_output(['pidof','-s','nn_crumbs']))
    os.kill(predictorPid, signal.SIGTERM)
    print('exiting app')

atexit.register(exitHandler)

frontend.wait()
sys.exit(0)
