import struct

def read_images(filename):
    with open(filename, 'rb') as f:
        magic, num_images, rows, cols = struct.unpack('>IIII', f.read(16))
        image_data = f.read()
        images = [list(image_data[i * rows * cols : (i + 1) * rows * cols]) for i in range(num_images)]
    return images

def read_labels(filename):
    with open(filename, 'rb') as f:
        magic, num_labels = struct.unpack('>II', f.read(8))
        labels = list(f.read())
    return labels

def save_arff(images, labels, output_file):
    with open(output_file, 'w') as f:
        f.write("@RELATION mnist\n\n")
        for i in range(784):
            f.write(f"@ATTRIBUTE pixel{i} NUMERIC\n")
        f.write("@ATTRIBUTE class {0,1,2,3,4,5,6,7,8,9}\n\n")
        f.write("@DATA\n")
        for pixels, label in zip(images, labels):
            normalized = [str(p / 255.0) for p in pixels] 
            line = ",".join(normalized) + f",{label}\n"
            f.write(line)


base_path = "./raw-data/"
train_images_file = base_path + "train-images.idx3-ubyte"
train_labels_file = base_path + "train-labels.idx1-ubyte"
test_images_file = base_path + "t10k-images.idx3-ubyte"
test_labels_file = base_path + "t10k-labels.idx1-ubyte"

train_images = read_images(train_images_file)
train_labels = read_labels(train_labels_file)
test_images = read_images(test_images_file)
test_labels = read_labels(test_labels_file)

save_arff(train_images, train_labels, "mnist_train.arff")
save_arff(test_images, test_labels, "mnist_test.arff")
print("Erfolgreich erstellt")

# TODO: Danach müssen die .arff Files in 'src/main/resources' kopiert werden
