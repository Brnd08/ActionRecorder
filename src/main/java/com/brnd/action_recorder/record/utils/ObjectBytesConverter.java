package com.brnd.action_recorder.record.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ObjectBytesConverter {
    private ObjectBytesConverter(){
        logger.log(Level.ERROR, "Utility classes should not be instantiated.");
    }
    private static final Logger logger = LogManager.getLogger(ObjectBytesConverter.class);

    /**
     * Deserialize a byte array into an object of the specified class type.
     *
     * @param byteArray The byte array to be deserialized.
     * @param clazz     The class type of the object to obtain.
     * @param <T>       The generic type of the object to be deserialized, must be a subtype of Serializable.
     * @return The deserialized object of type T.
     * @throws IOException            If an I/O error occurs while deserializing the byte array.
     * @throws ClassNotFoundException If the class of the deserialized object cannot be found.
     * @see Serializable
     */
    public static <T extends Serializable> T objectFromBytes(byte[] byteArray, Class<T> clazz) throws IOException, ClassNotFoundException {
        logger.log(Level.INFO, "Deserializing {} object from byte array", clazz.getName());
        T genericObject;
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(byteArray);
            ObjectInputStream oin = new ObjectInputStream(bin);
            genericObject = clazz.cast(oin.readObject());
            bin.close();
            oin.close();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR, "A problem has occurred while trying to convert bytes to Generic Object of class {}"
                    , clazz.getName()
            );
            throw e;
        }
        return genericObject;
    }

    /**
     * Serializes a specified object in to a byte array.
     *
     * @param genericObject The object to be serialized.
     * @return A byte[] containing the serialized object.
     * @param <T> The type of the object, must be a subtype of Serializable.
     * @throws IOException If and exception occur during object serialization.
     * @see Serializable
     */
    public static <T extends Serializable> byte[] toBytes(  T genericObject) throws IOException {
        logger.log(Level.INFO, "Serializing {} object into byte array", genericObject.getClass().getName());
        byte[] bytes ;
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream dataOutput = new ObjectOutputStream(byteOutput);
            dataOutput.writeObject(genericObject);
            bytes = byteOutput.toByteArray();
            dataOutput.close();
            byteOutput.close();
        } catch (IOException e) {
            logger.log(
                    Level.ERROR, "A problem has occurred while trying to convert Generic Object ({} object) to bytes[]"
                    , genericObject.getClass().getName()
            );
            throw e;
        }
        return bytes;
    }
}
