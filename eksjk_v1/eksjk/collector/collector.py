import os
import queue

from .listener import Listener

class Collector():
    patient = None
    study = None
    series = None

    def __init__(self):
        self.q = queue.Queue()
        self.need_stop = False

    def run(self):
        listener = Listener(self.q)
        listener.start()

        print('collector waiting...')
        while not self.need_stop:
            info = self.q.get()
            print('collector get:', info)
            if isinstance(info, str):
                if info == 'quit':
                    break

            self.parse_file(info['file_path'], info['user_id'])

        listener.join()

    def parse_file(self, file_path, user_id):
        pass

def run_collector():
    Collector().run()

if __name__ == "__main__":
    run_collector()