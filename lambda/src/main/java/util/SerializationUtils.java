package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.*;
import java.util.List;

public class SerializationUtils {
	public static byte[] serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(obj);
			return bos.toByteArray();
		}
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
				 ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		}
	}

	private byte[] serializeList(List<Integer> list) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(list);
			oos.flush();
			return bos.toByteArray();
		}
	}

	private List<Integer> deserializeList(byte[] listBytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(listBytes);
				 ObjectInputStream ois = new ObjectInputStream(bis)) {
			return (List<Integer>) ois.readObject();
		}
	}
}
