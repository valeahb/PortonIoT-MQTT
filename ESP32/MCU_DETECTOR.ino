
#include <WiFi.h>
#include <PubSubClient.h>

// Configuracion Wi-Fi
const char* ssid = "Wokwi-GUEST";
const char* password = "";

// Configuracion MQTT
const char* mqtt_server = "broker.hivemq.com";
const int mqtt_port = 1883;

const char* topic = "portoniot/detector/patente";

// Objetos de conexion
WiFiClient espClient;
PubSubClient client(espClient);

// Validar letras
bool esLetra(char c) {
  return c >= 'A' && c <= 'Z';
}

// Validar numeros
bool esNumero(char c) {
  return c >= '0' && c <= '9';
}

// Validar formato de patente
bool validarPatente(String patente) {

  if (patente.length() != 6) {
    return false;
  }

  bool formato1 = true;
  bool formato2 = true;
  bool formato3 = true;

  // Formato ABCD12
  for (int i = 0; i < 4; i++) {
    if (!esLetra(patente[i])) formato1 = false;
  }
  for (int i = 4; i < 6; i++) {
    if (!esNumero(patente[i])) formato1 = false;
  }

  // Formato AB1234
  for (int i = 0; i < 2; i++) {
    if (!esLetra(patente[i])) formato2 = false;
  }
  for (int i = 2; i < 6; i++) {
    if (!esNumero(patente[i])) formato2 = false;
  }

  // Formato ABC123
  for (int i = 0; i < 3; i++) {
    if (!esLetra(patente[i])) formato3 = false;
  }
  for (int i = 3; i < 6; i++) {
    if (!esNumero(patente[i])) formato3 = false;
  }

  return formato1 || formato2 || formato3;
}

// Conectar a Wi-Fi
void conectarWiFi() {

  Serial.print("Conectando a Wi-Fi");

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("Wi-Fi conectado.");
  Serial.print("Direccion IP: ");
  Serial.println(WiFi.localIP());
}

// Conectar a MQTT
void conectarMQTT() {

  while (!client.connected()) {

    Serial.print("Conectando al broker MQTT...");

    String clientId = "MCU_DETECTOR_";
    clientId += String((uint32_t)ESP.getEfuseMac(), HEX);

    if (client.connect(clientId.c_str())) {
      Serial.println("Conectado.");
    } else {
      Serial.print("Error MQTT: ");
      Serial.println(client.state());
      delay(3000);
    }
  }
}

void setup() {

  Serial.begin(115200);

  Serial.println("================================");
  Serial.println("          PORTON IoT");
  Serial.println("         MCU_DETECTOR");
  Serial.println("================================");

  conectarWiFi();

  client.setServer(mqtt_server, mqtt_port);

  conectarMQTT();

  Serial.println("Ingrese una patente:");
}

void loop() {

  if (!client.connected()) {
    conectarMQTT();
  }

  client.loop();

  if (Serial.available() > 0) {

    String patente = Serial.readStringUntil('\n');

    patente.trim();
    patente.toUpperCase();

    if (patente.length() > 0) {

      Serial.print("Patente recibida: ");
      Serial.println(patente);

      if (validarPatente(patente)) {

        Serial.println("Formato valido.");

        if (client.publish(topic, patente.c_str())) {
          Serial.println("Patente enviada por MQTT correctamente.");
        } else {
          Serial.println("Error al enviar la patente.");
        }

      } else {

        Serial.println("Formato de patente incorrecto.");
        Serial.println("No se envio por MQTT.");
      }
    }

    Serial.println("-------------------------------");
    Serial.println("Ingrese otra patente:");
  }
}
