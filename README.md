# Portón IoT – Evaluación

## Descripción del proyecto

Portón IoT es un sistema de control de acceso automatizado que permite verificar patentes vehiculares y controlar la apertura o rechazo de un portón mediante comunicación inalámbrica.

El sistema integra una aplicación Android, microcontroladores ESP32, comunicación MQTT y servicios de Firebase.

## Tecnologías utilizadas

- Android Studio y Java.
- ESP32.
- Wokwi.
- MQTT y broker HiveMQ.
- Python.
- Firebase Authentication.
- Firebase Realtime Database.
- Cloud Firestore.

## Arquitectura del sistema

1. La aplicación Android permite iniciar sesión y registrar patentes autorizadas.
2. MCU_DETECTOR captura e identifica la patente y la publica mediante MQTT.
3. Python recibe el mensaje y consulta la patente en Firebase Realtime Database.
4. El resultado de la validación se almacena en Cloud Firestore.
5. Python publica la respuesta MQTT.
6. MCU_PORTON recibe la respuesta y controla el servomotor, los LED y el buzzer.

## Escenarios de funcionamiento

**Acceso autorizado:**
- La patente está registrada en Firebase.
- El sistema envía la respuesta ABRIR.
- El servomotor abre el portón y se enciende el LED verde.

**Acceso rechazado:**
- La patente no está registrada.
- El sistema envía la respuesta RECHAZAR.
- El portón permanece cerrado, se enciende el LED rojo y suena el buzzer.

## Almacenamiento de información

- Firebase Realtime Database: usuarios y patentes autorizadas.
- Cloud Firestore: historial de mensajes MQTT y resultados de validación.

## Seguridad

Las credenciales privadas de Firebase no deben publicarse en este repositorio.

