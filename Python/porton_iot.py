import firebase_admin
from firebase_admin import credentials, db, firestore
import paho.mqtt.client as mqtt

# CONFIGURACION FIREBASE
cred = credentials.Certificate("firebase-key.json")

firebase_admin.initialize_app(cred, {
    "databaseURL": "https://portoniot-57167-default-rtdb.firebaseio.com/"
})
# CONEXION CON CLOUD FIRESTORE
db_firestore = firestore.client()

# CONFIGURACION MQTT
BROKER = "broker.hivemq.com"
PUERTO = 1883

TEMA_PATENTE = "portoniot/detector/patente"
TEMA_RESPUESTA = "portoniot/porton/respuesta"


def verificar_patente(patente):

    patente = patente.strip().upper()

    referencia = db.reference("patentes/" + patente)
    datos = referencia.get()

    if datos and datos.get("autorizada") == True:
        print("Patente autorizada:", patente)
        return "ABRIR"

    print("Patente rechazada:", patente)
    return "RECHAZAR"

def guardar_mensaje(patente, respuesta):

    datos = {
        "patente": patente,
        "autorizada": respuesta == "ABRIR",
        "respuesta": respuesta,
        "origen": "MQTT",
        "fecha": firestore.SERVER_TIMESTAMP
    }

    db_firestore.collection("mensajes_mqtt").add(datos)

    print("Mensaje guardado en Firestore.")

def al_conectar(client, userdata, flags, reason_code, properties):
    print("Conectado a MQTT")

    client.subscribe(TEMA_PATENTE)
    print("Esperando patentes...")


def al_recibir(client, userdata, mensaje):

    patente = mensaje.payload.decode("utf-8").strip().upper()

    print("\nPatente recibida:", patente)

    respuesta = verificar_patente(patente)

    # Guardar mensaje en Firestore
    guardar_mensaje(patente, respuesta)

    # Enviar respuesta al porton
    client.publish(TEMA_RESPUESTA, respuesta)

    print("Respuesta enviada:", respuesta)


cliente = mqtt.Client(
    callback_api_version=mqtt.CallbackAPIVersion.VERSION2
)

cliente.on_connect = al_conectar
cliente.on_message = al_recibir

print("Conectando con MQTT...")

cliente.connect(BROKER, PUERTO, 60)

cliente.loop_forever()
