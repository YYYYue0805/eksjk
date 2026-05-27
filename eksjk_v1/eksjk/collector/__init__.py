from django.conf import settings
import django

import wjwsjk.settings as config

INSTALLED_APPS = [
    'django.contrib.auth',
    'django.contrib.contenttypes',
]

configure = {
    'INSTALLED_APPS': INSTALLED_APPS,
    'DATABASES': config.DATABASES,
    'USE_TZ': config.USE_TZ,
    'TIME_ZONE': config.TIME_ZONE,
    'BASE_DIR': config.BASE_DIR,
}

settings.configure(**configure)
django.setup()

from multiprocessing import  Process
from .collector import run_collector

def run():
    p = Process(target=run_collector)
    p.start()
    return p
