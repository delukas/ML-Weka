# WEKA-ML: Eine Beispielimplementation

## Anforderungen
- Maven installiert (über IntelliJ im Maven Tab Rechts)
- Java
- Python

## Installation
- Maven Dependencies aus der pom.xml müssen heruntergeladen werden. (`mvn install`)

## Ausführen
- In IntelliJ sollte ein Profil "ML-Weka" hinterlegt sein, was über den Play-Button ausgeführt werden kann.
- Ansonsten in der Konsole: `mvn clean compile exec:java`

## Hinweise
Wenn ein Modell in 'src/main/resources' vorhanden ist, kann man dieses über die UI einfach durch `Modell laden` schnell laden. Wenn das Modell neu trainiert werden soll, muss das Modell in diesem Ordner gelöscht werden, die ARFF Dateien dort verfügbar sein, die UI neugestartet werden und dann durch `Modell trainieren` trainiert werden. (Dauert deutlich länger [ca. 5-20 Minuten]). Danach ist das Modell sofort in der UI verfügbar und wird genau diesem Ort gespeichert. Nach dem trainieren wird in der Konsole automatisch eine Evaluation durchgeführt.