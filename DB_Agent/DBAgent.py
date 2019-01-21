import paho.mqtt.client as mqtt
import json

from DBManager import DatabaseManager

MQTT_Broker = "m15.cloudmqtt.com"
MQTT_Port = 14558
Keep_Alive_Interval = 45
MQTT_Topic = "ITM/#"
WorkSession_topic = "ITM/Workbench1/Worksession"
Lum_topic = "ITM/Workbench1/Enviroment/lum"
Hum_topic = "ITM/Workbench1/Enviroment/hum"
Temp_topic = "ITM/Workbench1/Enviroment/temp"
Product_topic = "ITM/Workbench1/product"
MQTT_UserName = "gxovygso"
MQTT_pass = "0P7X_9cvhzzV"

def on_connect(client, userdata, flags, rc):
    print("rc: " + str(rc))

def on_message(client, obj, msg):
    print("The Topic:" + " " + msg.topic + " , " + "The Messege:" + " " + str(msg.payload))
    Data_Handler(msg.topic, msg.payload)

def on_subscribe(client, obj, mid, granted_qos):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))

def Data_Handler_Worksession(Data):
    JsonDictionary = json.loads(Data)
    RFID_value = JsonDictionary['v']
    dbObj = DatabaseManager()
    dbObj.cur.execute("select rfid FROM worksession order by sessionid DESC limit 1")
    rows = dbObj.cur.fetchone()
    for r in rows:
        if(RFID_value != r):
            dbObj.add_del_update_db_record("insert into WorkSession(RFID) VALUES (%s)", [RFID_value])
            del dbObj

def Data_Handler_Enviroment(Data):
    JsonDictionary = json.loads(Data)
    Name = JsonDictionary["n"]
    Value = JsonDictionary["v"]
    Time = JsonDictionary["t"]
    Date = JsonDictionary["d"]
    dbObj = DatabaseManager()
    dbObj.cur.execute("SELECT SessionID FROM Worksession ORDER BY SessionID DESC LIMIT 1")
    rows = dbObj.cur.fetchall()
    dbObj.add_del_update_db_record("insert into enviroment(sensor_name,Sensor_value,time,date,sessionid) VALUES (%s,%s,%s,%s,%s)",
    [Name, Value, Time, Date, rows])

    del dbObj

def Data_Handler_Product(Data):
    JsonDictionary = json.loads(Data)
    ProductCount = JsonDictionary["v"]
    dbObj = DatabaseManager()
    dbObj.cur.execute("SELECT SessionID FROM Worksession ORDER BY SessionID DESC LIMIT 1")
    rows = dbObj.cur.fetchall()
    dbObj.add_del_update_db_record("insert into Product(Product_sessionID,Product_Count)VALUES (%s,%s)",
    [rows ,ProductCount])
    del dbObj

def Data_Handler(Topic, jsonData):
   if(Topic == WorkSession_topic):
       Data_Handler_Worksession(jsonData)
   elif (Topic == Product_topic):
       Data_Handler_Product(jsonData)
   elif(Topic == Lum_topic or Topic == Hum_topic or Temp_topic):
       Data_Handler_Enviroment(jsonData)



mqttc = mqtt.Client()

# Assign event callbacks
mqttc.on_message = on_message
mqttc.on_connect = on_connect
mqttc.on_subscribe = on_subscribe

# Connect
mqttc.username_pw_set(MQTT_UserName, MQTT_pass)
mqttc.connect(MQTT_Broker, MQTT_Port, Keep_Alive_Interval)
mqttc.subscribe(MQTT_Topic, 0)

# Continue the network loop
rc = 0
while rc == 0:
    rc = mqttc.loop()
print("rc: " + str(rc))

