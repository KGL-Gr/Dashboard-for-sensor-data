import requests
from bs4 import BeautifulSoup
import time 
import re
from datetime import datetime

url = 'http://192.168.1.6/'
day = datetime.now()
print(day.strftime("%d/%m"))

for i in range(10):

    now = datetime.now()
    current_time = now.strftime("%H:%M")
    print("Time is: ",current_time)

    page = requests.get(url)
    soup = BeautifulSoup(page.content, 'html.parser')

    humidity = str(soup.find(id = 'humidity'))
    temperature = str(soup.find(id = 'temperature'))
    
    while(humidity == '<span id="humidity">--</span>' or temperature == '<span id="temperature">--</span>'):
        time.sleep(5)

        page = requests.get(url)
        soup = BeautifulSoup(page.content, 'html.parser')

        humidity = str(soup.find(id = 'humidity'))
        temperature = str(soup.find(id = 'temperature'))

    humidity = re.findall("[0-9]+.[0-9]+", humidity)
    temperature = re.findall("[0-9]+.[0-9]+", temperature)

    print("Humidity is: ",humidity[0])
    print("Temperature is: ",temperature[0])
    time.sleep(5)   # Measures in secs.
