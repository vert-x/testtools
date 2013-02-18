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
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This class delegates to the JUnit Assert class but catches any AssertionError instances that are thrown and
 * sends them back to the VertxJunitTestRunner class so they can be rethrown to JUnit.
 */
public class VertxAssert {

  private static Vertx vertx;
  public static void initialize(Vertx vertx) {
    VertxAssert.vertx = vertx;
  }

  private static void handleError(AssertionError e) {
    if (vertx == null) {
      throw new IllegalStateException("Please initialise VertxAssert before use");
    }
    //Serialize the error
    byte[] bytes;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(e);
      oos.flush();
      bytes = baos.toByteArray();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to serialise error");
    }
    vertx.eventBus().send(JavaClassRunner.TESTRUNNER_HANDLER_ADDRESS, new JsonObject().putString("type", "failure").putBinary("failure", bytes));
  }

  public static void testComplete() {
    vertx.eventBus().send(JavaClassRunner.TESTRUNNER_HANDLER_ADDRESS, new JsonObject().putString("type", "done"));
  }

  public static void assertTrue(String message, boolean condition) {
    try {
      Assert.assertTrue(message, condition);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertFalse(boolean condition) {
    try {
      Assert.assertFalse(condition);
    } catch (AssertionError e) {
      handleError(e);
    }

  }

  public static void assertArrayEquals(String message, char[] expecteds, char[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertSame(String message, Object expected, Object actual) {
    try {
      Assert.assertSame(message, expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(long expected, long actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNull(Object object) {
    try {
      Assert.assertNull(object);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertFalse(String message, boolean condition) {
    try {
      Assert.assertFalse(message, condition);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void fail(String message) {
    try {
      Assert.fail(message);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNull(String message, Object object) {
    try {
      Assert.assertNull(message, object);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, float[] expecteds, float[] actuals, float delta) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  @Deprecated
  public static void assertEquals(String message, double expected, double actual) {
    try {
      Assert.assertEquals(message, expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }


  public static void assertArrayEquals(String message, double[] expecteds, double[] actuals, double delta) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, Object[] expecteds, Object[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, short[] expecteds, short[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(short[] expecteds, short[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(long[] expecteds, long[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNotNull(Object object) {
    try {
      Assert.assertNotNull(object);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(Object expected, Object actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(String message, Object expected, Object actual) {
    try {
      Assert.assertEquals(message, expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertTrue(boolean condition) {
    try {
      Assert.assertTrue(condition);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNotNull(String message, Object object) {
    try {
      Assert.assertNotNull(message, object);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(String message, double expected, double actual, double delta) {
    try {
      Assert.assertEquals(message, expected, actual, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void fail() {
    try {
      Assert.fail();
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertSame(Object expected, Object actual) {
    try {
      Assert.assertSame(expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(String message, long expected, long actual) {
    try {
      Assert.assertEquals(message, expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, byte[] expecteds, byte[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, long[] expecteds, long[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertEquals(double expected, double actual, double delta) {
    try {
      Assert.assertEquals(expected, actual, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static <T> void assertThat(T actual, Matcher<T> matcher) {
    try {
      Assert.assertThat(actual, matcher);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  @Deprecated
  public static void assertEquals(String message, Object[] expecteds, Object[] actuals) {
    try {
      Assert.assertEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  @Deprecated
  public static void assertEquals(Object[] expecteds, Object[] actuals) {
    try {
      Assert.assertEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNotSame(String message, Object unexpected, Object actual) {
    try {
      Assert.assertNotSame(message, unexpected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static <T> void assertThat(String reason, T actual, Matcher<T> matcher) {
    try {
      Assert.assertThat(reason, actual, matcher);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(float[] expecteds, float[] actuals, float delta) {
    try {
      Assert.assertArrayEquals(expecteds, actuals, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertNotSame(Object unexpected, Object actual) {
    try {
      Assert.assertNotSame(unexpected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(char[] expecteds, char[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(double[] expecteds, double[] actuals, double delta) {
    try {
      Assert.assertArrayEquals(expecteds, actuals, delta);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(int[] expecteds, int[] actuals) {
    try {
      Assert.assertArrayEquals(expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  @Deprecated
  public static void assertEquals(double expected, double actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      handleError(e);
    }
  }

  public static void assertArrayEquals(String message, int[] expecteds, int[] actuals) throws ArrayComparisonFailure {
    try {
      Assert.assertArrayEquals(message, expecteds, actuals);
    } catch (AssertionError e) {
      handleError(e);
    }
  }
}
