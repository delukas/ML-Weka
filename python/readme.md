
### Anforderungen
- Datensatz herunterladen: https://www.kaggle.com/datasets/hojjatk/mnist-dataset entpacken und die Dateien aus dem Zip-Archiv in den `raw-data`-Ordner einfügen.
- Es sollte eine Virtuelle Enviroment für Python aufzusetzen, da für das Validierungsfile externe Bibliotheken verwendet werden.

### ENV initialisieren:
```bash
python -m venv .env
```

### ENV aktivieren:
```bash
./.env/Scripts/activate
```

### Bibliotheken herunterladen:
```bash
pip install pandas matplotlib scipy numpy
```

### ARFF Files erstellen:
```bash
python main.py
```
Danach müssen die .arff Files in 'src/main/resources' kopiert werden, damit Weka diese finden kann.

### ARFF Files darstellen:
```bash
python validate.py
```
Wie viele Bilder geplottet werden, hängt von der range-Schleife ab.