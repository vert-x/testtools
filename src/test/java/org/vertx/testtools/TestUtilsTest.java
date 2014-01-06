package org.vertx.testtools;

import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.buffer.Buffer;

import java.lang.reflect.Array;

/**
 * Tests for TestUtils
 */
public class TestUtilsTest {
    @Test
    public void testGenerateRandomBufferSimple() {
        Buffer buffer = TestUtils.generateRandomBuffer(1000);
        Assert.assertTrue("Buffer of unexpected size.", buffer.length() == 1000);
    }

    @Test
    public void testGenerateRandomBuffer() {
        // This isn't really a very good test, since we could generate
        // a thousand random buffers that never include (byte)65 yet still
        // not hit the edge case. But for completeness' sake, let's do this.
        Buffer buffer = TestUtils.generateRandomBuffer(1000, true, (byte) 65);
        Assert.assertTrue("Buffer of unexpected size.", buffer.length() == 1000);
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Buffer shouldn't contain (byte) 65", buffer.getByte(i) != 65);
        }
    }

    @Test
    public void testGenerateRandomByteArraySimple() {
        byte[] buffer = TestUtils.generateRandomByteArray(1000);
        Assert.assertTrue("Byte array of unexpected size.", buffer.length == 1000);
    }

    @Test
    public void testGenerateRandomByteArray() {
        // This isn't really a very good test, see above
        byte[] buffer = TestUtils.generateRandomByteArray(1000, true, (byte) 65);
        Assert.assertTrue("Buffer of unexpected size.", buffer.length == 1000);
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Buffer shouldn't contain (byte) 65", buffer[i] != 65);
        }
    }

    @Test
    public void testRandomUnicodeString() {
        String string = TestUtils.randomUnicodeString(1000);
        Assert.assertTrue("String of unexpected size.", string.length() == 1000);
        for (int i = 0; i < 1000; i++) {
            char c = string.charAt(i);
            Assert.assertTrue("Non-unicode character found", c != 0xFFFE && c != 0xFFFF && (c > 0xD800 || c <= 0xDFFF));
        }
    }

    @Test
    public void testRandomAlphaString() {
        String string = TestUtils.randomAlphaString(1000);
        Assert.assertTrue("String of unexpected size.", string.length() == 1000);
        for (int i = 0; i < 1000; i++) {
            char c = string.charAt(i);
            Assert.assertTrue("Non-alpha character found", c >= 65 && c <= 122);
        }
    }

    @Test
    public void testBuffersEqual() {
        Buffer buffer1 = TestUtils.generateRandomBuffer(1000);
        Buffer buffer2 = new Buffer();
        Assert.assertTrue("Buffers should not be equal", !TestUtils.buffersEqual(buffer1, buffer2));
        buffer2 = buffer1.copy();
        Assert.assertTrue("Buffers should be equal", TestUtils.buffersEqual(buffer1, buffer2));
    }

    @Test
    public void testByteArraysEqual() {
        byte[] array1 = TestUtils.generateRandomByteArray(1000);
        byte[] array2 = new byte[1000];
        Assert.assertTrue("Arrays should not be equal", !TestUtils.byteArraysEqual(array1, array2));
        array2 = array1.clone();
        Assert.assertTrue("Arrays should be equal", TestUtils.byteArraysEqual(array1, array2));
    }
}
