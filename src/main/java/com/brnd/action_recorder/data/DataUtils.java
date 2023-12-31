/*
 * Copyright (C) 2023 Brandon Velazquez & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.brnd.action_recorder.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class hold utilities related to data saving, retrieving and management
 */
public class DataUtils {
    
    private DataUtils(){
        logger.log(Level.ERROR, "Utility classes should not be instantiated.");
    }
    private static final Logger logger = LogManager.getLogger(DataUtils.class);
    
    public static void logSuppressedExceptions(Logger logger, Throwable[] suppressedExceptions){
         for(var suppressedException : suppressedExceptions)
                logger.log(Level.FATAL, "An error occurred while closing resources. Exception message: {}", suppressedException.getMessage());
    }
    
    
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
