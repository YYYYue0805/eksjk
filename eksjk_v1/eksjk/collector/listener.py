import threading
import zmq
from django.conf.settings import COLLECTOR_HOST

class Listener(threading.Thread):
    def __init__(self, queue):
        threading.Thread.__init__(self, name='listener')

        self.queue = queue
        self.stop = False

    def run(self):
        context = zmq.Context()
        socket = context.socket(zmq.REP)
        socket.bind(COLLECTOR_HOST)
        print('Collector Listener run:', COLLECTOR_HOST)

        while not self.stop:
            message = socket.recv_json()
            print('recv:', message)
            self.queue.put(message)
            socket.send_string('ok')

    def cancel(self):
        self.stop = True