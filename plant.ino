#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

#include <ESP8266HTTPClient.h>

#include <WiFiClient.h>

#include <WiFiClientSecureBearSSL.h>

ESP8266WiFiMulti WiFiMulti;

int sValue = 0;
const uint8_t fingerprint[20] = {0x6F, 0xD0, 0x9A, 0x52, 0xC0, 0xE9, 0xE4, 0xCD, 0xA0, 0xD3, 0x02, 0xA4, 0xB7, 0xA1, 0x92, 0x38, 0x2D, 0xCA, 0x2F, 0x26};
void setup() {
  Serial.begin(115200);

  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("SSID", "SSID_Password");

  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
}

void loop() {

  int sValue1 = analogRead(A0);
  Serial.println(sValue);
  
  if ((WiFiMulti.run() == WL_CONNECTED)) {
    HTTPClient http2;

    WiFiClient client2;

    if(sValue1 > 735 && sValue < 735){
      if (http2.begin(client2, "http://fcm.googleapis.com/fcm/send")) {  // HTTP
      Serial.print("[HTTP] GET...\n");
      // start connection and send HTTP header
      http2.addHeader("Authorization", "key=<>");
      http2.addHeader("Content-type", "application/json");
      int httpCode2 = http2.POST("{\"to\": \"/topics/all\", \"notification\":{\"body\":\"Du solltest deine Pflanze gießen\"}}");

      // httpCode will be negative on error
      if (httpCode2 > 0) {
        // HTTP header has been send and Server response header has been handled
        Serial.printf("[HTTP] GET... code: %d\n", httpCode2);

        // file found at server
        if (httpCode2 == HTTP_CODE_OK || httpCode2 == HTTP_CODE_MOVED_PERMANENTLY) {
          String payload2 = http2.getString();
          Serial.println(payload2);
        }
      } else {
        Serial.printf("[HTTP] GET... failed, error: %s\n", http2.errorToString(httpCode2).c_str());
      }

      http2.end();
    } else {
      Serial.printf("[HTTP} Unable to connect\n");
    }
    }
    client2.stop();

    sValue = sValue1;

    std::unique_ptr<BearSSL::WiFiClientSecure> client(new BearSSL::WiFiClientSecure);
    client->setFingerprint(fingerprint);
    HTTPClient http;

    Serial.print("[HTTP] begin...\n");
    if (http.begin(*client, "https://smarthome-7727f.firebaseio.com/Galerie.json")) {  // HTTP
      Serial.print("[HTTP] GET...\n");
      // start connection and send HTTP header
      int httpCode = http.PUT("{\"humindity\": " + String(sValue) + "}");

      // httpCode will be negative on error
      if (httpCode > 0) {
        // HTTP header has been send and Server response header has been handled
        Serial.printf("[HTTP] GET... code: %d\n", httpCode);

        // file found at server
        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          String payload = http.getString();
          Serial.println(payload);
        }
      } else {
        Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
      }

      http.end();
    } else {
      Serial.printf("[HTTP} Unable to connect\n");
    }
  }

  delay(10000);
}
