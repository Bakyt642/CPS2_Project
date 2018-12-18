import json

import paho.mqtt.client as mqtt
from DB_Agent.DB_Connection import DatabaseManager1

MQTT_Broker = "m15.cloudmqtt.com"
MQTT_Port = 14558
Keep_Alive_Interval = 45
MQTT_Topic = "ITM"
WorkSession_topic = "ITM/Workbench1/Worksession"
Enviroment_topic = "ITM/Workbench1/Enviroment"
Product_topic = "ITM/Workbenche1/Product"
MQTT_UserName = "gxovygso"
MQTT_pass = "0P7X_9cvhzzV"

def on_connect(client, userdata, flags, rc):
    print("rc: " + str(rc))

def on_message(client, obj, msg):
    MessegePayload = float(msg.payload)
    print("The Topic:" + " " + msg.topic + " , " + "The Messege:" + " " + str(MessegePayload))
    Data_Handler(msg.topic, MessegePayload)

def on_subscribe(client, obj, mid, granted_qos):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))

def Data_Handler_Worksession(Data):
    JsonDictionary = json.loads(Data)
    RFID_value = JsonDictionary['v']
    dbObj = DatabaseManager1()
    dbObj.add_del_update_db_record("insert into WorkSession(RFID) VALUES (?)", [RFID_value])
    del dbObj

def Data_Handler_Enviroment(Data):
    JsonDictionary = json.loads(Data)
    Name = JsonDictionary['n']
    Value = JsonDictionary['v']
    Time = JsonDictionary['t']
    Date = JsonDictionary['d']
    dbObj = DatabaseManager1()

    dbObj.add_del_update_db_record("insert into Enviroment(Sensor_Name,Sensor_value,Time,Date,SessionID) VALUES (?,?,?,?,?)",
    [Name, Value, Time, Date,
    (dbObj.add_del_update_db_record("select SessionID FROM Worksession ORDER BY SessionID DESC LIMIT 1"))])
    del dbObj

def Data_Handler_Product(Data):
    JsonDictionary = json.loads(Data)
    ProductCount = JsonDictionary["v"]
    dbObj = DatabaseManager1()
    dbObj.add_del_update_db_record("insert into Product(Product_sessionID,Product_Count) VALUES (?,?)",
    [(dbObj.add_del_update_db_record("SELECT SessionID FROM Worksession ORDER BY SessionID DESC LIMIT 1"))
    ,ProductCount])
    del dbObj

def Data_Handler(Topic, jsonData):
   if(Topic == WorkSession_topic):
       ## messege = {"n":"RFID","v":" "}
       Data_Handler_Worksession(jsonData)
   elif(Topic == Enviroment_topic):
       ## Topic = "ITM/Workbench1/Enviroment/hum" or "ITM/Workbench1/Enviroment/temp" or "ITM/Workbench1/Enviroment/lumi"
       ## humidity for example:
       ## messege = {"n":"hum","v":" ","t":" ","d":" "}
       Data_Handler_Enviroment(jsonData)
       ## messege = {"v":" "}
   elif(Topic == Product_topic):
       Data_Handler_Product(jsonData)



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


