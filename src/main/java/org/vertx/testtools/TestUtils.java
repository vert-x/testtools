package org.vertx.testtools;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

import org.vertx.java.core.buffer.Buffer;

/**
 * Static helper methods for dealing with buffers, strings and byte arrays.
 */
public class TestUtils {


    /**
     * Creates a Buffer of random bytes.
     * @param length The length of the Buffer
     * @return the Buffer
     */
    public static Buffer generateRandomBuffer(int length) {
      return generateRandomBuffer(length, false, (byte) 0);
    }

    /**
     * Create an array of random bytes
     * @param length The length of the created array
     * @return the byte array
     */
    public static byte[] generateRandomByteArray(int length) {
      return generateRandomByteArray(length, false, (byte) 0);
    }

    /**
     * Create an array of random bytes
     * @param length The length of the created array
     * @param avoid If true, the resulting array will not contain avoidByte
     * @param avoidByte A byte that is not to be included in the resulting array
     * @return an array of random bytes
     */
    public static byte[] generateRandomByteArray(int length, boolean avoid, byte avoidByte) {
      byte[] line = new byte[length];
      for (int i = 0; i < length; i++) {
        byte rand;
        do {
          rand = (byte) ((int) (Math.random() * 255) - 128);
        } while (avoid && rand == avoidByte);

        line[i] = rand;
      }
      return line;
    }

    /**
     * Creates a Buffer containing random bytes
     * @param length the size of the Buffer to create
     * @param avoid if true, the resulting Buffer will not contain avoidByte
     * @param avoidByte A byte that is not to be included in the resulting array
     * @return a Buffer of random bytes
     */
    public static Buffer generateRandomBuffer(int length, boolean avoid, byte avoidByte) {
      byte[] line = generateRandomByteArray(length, avoid, avoidByte);
      return new Buffer(line);
    }

    /**
     * Creates a String containing random unicode characters
     * @param length The length of the string to create
     * @return a String of random unicode characters
     */
    public static String randomUnicodeString(int length) {
      StringBuilder builder = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
        char c;
        do {
          c = (char) (0xFFFF * Math.random());
        } while ((c >= 0xFFFE && c <= 0xFFFF) || (c >= 0xD800 && c <= 0xDFFF)); //Illegal chars
        builder.append(c);
      }
      return builder.toString();
    }

    /**
     * Creates a random string of ascii alpha characters
     * @param length the length of the string to create
     * @return a String of random ascii alpha characters
     */
    public static String randomAlphaString(int length) {
      StringBuilder builder = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
        char c = (char) (65 + 25 * Math.random());
        builder.append(c);
      }
      return builder.toString();
    }

    /**
     * Determine if two Buffer objects are equal
     * @param b1 The first buffer to compare
     * @param b2 The second buffer to compare
     * @return true if the Buffers are equal
     */
    public static boolean buffersEqual(Buffer b1, Buffer b2) {
      if (b1.length() != b2.length()) return false;
      for (int i = 0; i < b1.length(); i++) {
        if (b1.getByte(i) != b2.getByte(i)) return false;
      }
      return true;
    }

    /**
     * Determine if two byte arrays are equal
     * @param b1 The first byte array to compare
     * @param b2 The second byte array to compare
     * @return true if the byte arrays are equal
     */
    public static boolean byteArraysEqual(byte[] b1, byte[] b2) {
      if (b1.length != b2.length) return false;
      for (int i = 0; i < b1.length; i++) {
        if (b1[i] != b2[i]) return false;
      }
      return true;
    }

    private TestUtils() {
        // no need to instantiate this class
    }
}
