import numpy as np
import matplotlib.pyplot as plt
from scipy.io import arff
import pandas as pd

# GPT generiert
# === Datei laden ===
data, meta = arff.loadarff('mnist_train.arff')
df = pd.DataFrame(data)

# Optional: letzte Spalte ist nominal (z. B. b'7') → in String/Int umwandeln
if isinstance(df.iloc[0, -1], bytes):
    df.iloc[:, -1] = df.iloc[:, -1].apply(lambda x: int(x.decode("utf-8")))

for index in range(100):
    sample = df.iloc[index]
    label = sample[-1]  # letzte Spalte = Label
    pixels = np.array(sample[:-1], dtype=np.float32).reshape((28, 28))

    # === Bild anzeigen ===
    plt.imshow(pixels, cmap='gray')
    plt.title(f"Label: {label} (Index: {index})")
    plt.axis('off')
    plt.show()
