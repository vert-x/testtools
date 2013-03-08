package org.vertx.testtools;/*
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

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class intercepts the running of the JUnit test and instead deploys the TestVerticle inside the Vert.x container
 * and runs the test on that instance.
 *
 * Any failures are propagated from the container back to this test runner using the Vert.x event bus where they
 * are rethrown to appear to have occurred locally.
 *
 */
public class JavaClassRunner extends BlockJUnit4ClassRunner {

  public static final String TESTRUNNER_HANDLER_ADDRESS = "vertx.testframework.handler";

  private static final Logger log = LoggerFactory.getLogger(JavaClassRunner.class);

  protected static final long TIMEOUT;
  private static final long DEFAULT_TIMEOUT = 300;
  static {
    String timeout = System.getProperty("vertx.test.timeout");
    TIMEOUT = timeout == null ? DEFAULT_TIMEOUT : Long.valueOf(timeout);
  }

  private final PlatformManager mgr;
  protected String main;
  private TestVerticleInfo annotation;

  public JavaClassRunner(Class<?> klass) throws InitializationError {
    super(klass);
    mgr = PlatformLocator.factory.createPlatformManager();
    setTestProperties();
  }

  private void setTestProperties() {
    // We set the properties here, rather than letting the build script do it
    // This means tests can run directly in an IDE with the correct properties set
    // without having to create custom test configurations
    File propsFile = new File("vertx.properties");
    if (propsFile.exists()) {
      Properties props = new Properties();
      try (InputStream is = new FileInputStream(propsFile.getName())) {
        props.load(is);
        for (String propName: props.stringPropertyNames()) {
          String propVal = props.getProperty(propName);
          System.setProperty("vertx." + propName, propVal);
        }
        String moduleName= props.getProperty("modowner") + "~" +
                           props.getProperty("modname") + "~" + props.getProperty("version");
        System.setProperty("vertx.modulename", moduleName);
      } catch (IOException e) {
        log.error("Failed to load props file", e);
      }
    }
  }

  protected TestVerticleInfo getAnnotation() {
    if (annotation == null) {
      Class<?> testClass = getTestClass().getJavaClass();
      Annotation[] anns = testClass.getAnnotations();
      for (Annotation aann: anns) {
        if (aann instanceof TestVerticleInfo) {
          TestVerticleInfo tann = (TestVerticleInfo)aann;
          annotation = tann;
        }
      }
    }
    return annotation;
  }


  protected List<FrameworkMethod> computeTestMethods() {
    Class<?> testClass = getTestClass().getJavaClass();
    if (!(TestVerticle.class.isAssignableFrom(testClass))) {
      throw new IllegalArgumentException("Test classes must extend TestVerticle");
    }
    this.main = testClass.getName();
    return getTestMethods();
  }

  protected List<FrameworkMethod> getTestMethods() {
    return super.computeTestMethods();
  }

  protected String getMain(String methodName) {
    return main;
  }

  public String getActualMethodName(String methodName) {
    return methodName;
  }

  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    Class<?> testClass = getTestClass().getJavaClass();
    String methodName = method.getName();
    Description desc = Description.createTestDescription(testClass, methodName);
    notifier.fireTestStarted(desc);
    final AtomicReference<Throwable> failure = new AtomicReference<>();
    try {
      JsonObject conf = new JsonObject().putString("methodName", getActualMethodName(methodName));
      final CountDownLatch testLatch = new CountDownLatch(1);
      Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
        @Override
        public void handle(Message<JsonObject> msg) {
          JsonObject jmsg = msg.body;
          String type = jmsg.getString("type");
          switch (type) {
            case "done":
              break;
            case "failure":
              //System.out.println("*** GOT A FAILURE");
              byte[] bytes = jmsg.getBinary("failure");
              // Deserialize
              try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                Throwable t = (Throwable)ois.readObject();
                // We display this since otherwise Gradle doesn't display it to stdout/stderr
                t.printStackTrace();
                failure.set(t);
              } catch (ClassNotFoundException | IOException e) {
                throw new IllegalArgumentException("Failed to deserialise error");
              }
              break;
          }
          testLatch.countDown();
        }
      };
      EventBus eb = mgr.getVertx().eventBus();
      eb.registerHandler(TESTRUNNER_HANDLER_ADDRESS, handler);
      final CountDownLatch deployLatch = new CountDownLatch(1);
      final AtomicReference<String> deploymentIDRef = new AtomicReference<>();
      String includes;
      TestVerticleInfo annotation = getAnnotation();
      if (annotation != null) {
        includes = getAnnotation().includes().trim();
        if (includes.isEmpty()) {
          includes = null;
        }
      } else {
        includes = null;
      }
      mgr.deployVerticle(getMain(methodName), conf, new URL[0], 1, includes, new Handler<String>() {
        public void handle(String deploymentID) {
          deploymentIDRef.set(deploymentID);
          deployLatch.countDown();
        }
      });
      waitForLatch(deployLatch);
      waitForLatch(testLatch);
      eb.unregisterHandler(TESTRUNNER_HANDLER_ADDRESS, handler);
      final CountDownLatch undeployLatch = new CountDownLatch(1);
      mgr.undeploy(deploymentIDRef.get(), new Handler<Void>() {
        public void handle(Void v) {
          undeployLatch.countDown();
        }
      });
      waitForLatch(undeployLatch);

      if (failure.get() != null) {
        notifier.fireTestFailure(new Failure(desc, failure.get()));
      } else {
        notifier.fireTestFinished(desc);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void waitForLatch(CountDownLatch latch) {
    while (true) {
      try {
        if (!latch.await(TIMEOUT, TimeUnit.SECONDS)) {
          throw new AssertionError("Timed out waiting for test to complete");
        }
        break;
      } catch (InterruptedException e) {
        // Ignore
      }
    }
  }



}
