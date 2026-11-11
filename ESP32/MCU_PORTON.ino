
#include <WiFi.h>
#include <PubSubClient.h>
#include <ESP32Servo.h>

// Wi-Fi
const char* ssid = "Wokwi-GUEST";
const char* password = "";

// Broker MQTT
const char* mqtt_server = "broker.hivemq.com";
const int mqtt_port = 1883;

// Temas MQTT
const char* topic_patente = "portoniot/detector/patente";
const char* topic_respuesta = "portoniot/porton/respuesta";

// Pines
#define SERVO_PIN 18
#define LED_VERDE 25
#define LED_ROJO 26
#define BUZZER 27

WiFiClient espClient;
PubSubClient client(espClient);
Servo porton;

// Controlar apertura
void abrirPorton() {

    Serial.println("ACCESO AUTORIZADO");

    digitalWrite(LED_ROJO, LOW);
    digitalWrite(BUZZER, LOW);
    digitalWrite(LED_VERDE, HIGH);

    porton.write(90);

    Serial.println("Porton abierto.");

    delay(3000);

    porton.write(0);
    digitalWrite(LED_VERDE, LOW);

    Serial.println("Porton cerrado.");
}

// Rechazar acceso
void rechazarAcceso() {

    Serial.println("ACCESO RECHAZADO");

    porton.write(0);

    digitalWrite(LED_VERDE, LOW);
    digitalWrite(LED_ROJO, HIGH);
    digitalWrite(BUZZER, HIGH);

    delay(1000);

    digitalWrite(BUZZER, LOW);

    Serial.println("Porton permanece cerrado.");

    delay(1500);

    digitalWrite(LED_ROJO, LOW);
}

// Recibir mensajes MQTT
void recibirMensaje(char* topic, byte* payload,
                    unsigned int length) {

    String mensaje = "";

    for (unsigned int i = 0; i < length; i++) {
        mensaje += (char)payload[i];
    }

    mensaje.trim();

    Serial.println("----------------------------");
    Serial.print("Tema recibido: ");
    Serial.println(topic);

    Serial.print("Respuesta recibida: ");
    Serial.println(mensaje);

    if (String(topic) == topic_respuesta) {

        if (mensaje == "ABRIR") {
            abrirPorton();
        }
        else if (mensaje == "RECHAZAR") {
            rechazarAcceso();
        }
    }
}

// Conectar Wi-Fi
void conectarWiFi() {

    Serial.print("Conectando a Wi-Fi");

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("\nWi-Fi conectado.");
}

// Conectar MQTT
void conectarMQTT() {

    while (!client.connected()) {

        Serial.print("Conectando a MQTT...");

        String clientId = "MCU_PORTON_VALE_2026_";
        clientId += String(random(0xFFFF), HEX);

        if (client.connect(clientId.c_str())) {
          Serial.println("Conectado al broker.");

          if (client.subscribe(topic_respuesta)) {
            Serial.println("Suscripcion correcta.");
          } else {
            Serial.println("Error al suscribirse.");
          }

        } else {
          Serial.print("Error MQTT: ");
          Serial.println(client.state());
          delay(3000);
        }
    }
}

void setup() {

    Serial.begin(115200);

    pinMode(LED_VERDE, OUTPUT);
    pinMode(LED_ROJO, OUTPUT);
    pinMode(BUZZER, OUTPUT);

    porton.attach(SERVO_PIN);
    porton.write(0);

    digitalWrite(LED_VERDE, LOW);
    digitalWrite(LED_ROJO, LOW);
    digitalWrite(BUZZER, LOW);

    Serial.println("==============================");
    Serial.println("          PORTON IoT");
    Serial.println("          MCU_PORTON");
    Serial.println("==============================");

    conectarWiFi();

    client.setServer(mqtt_server, mqtt_port);
    client.setCallback(recibirMensaje);

    conectarMQTT();
}

void loop() {
    if (!client.connected()) {
        Serial.print("Conexion MQTT perdida. Estado: ");
        Serial.println(client.state());
        conectarMQTT();
    }

    client.loop();
    delay(10);
}
