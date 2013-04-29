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

import org.junit.runner.RunWith;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(JavaClassRunner.class)
public abstract class TestVerticle extends Verticle {

  private static final Logger log = LoggerFactory.getLogger(TestVerticle.class);

  public void start() {
    initialize();
    startTests();
  }

  protected void initialize() {
    VertxAssert.initialize(vertx);
  }

  protected void startTests() {
    String methodName = container.config().getString("methodName");
    try {
      Method m = getClass().getDeclaredMethod(methodName);
      m.invoke(this);
    } catch (InvocationTargetException e) {
      InvocationTargetException it = (InvocationTargetException)e;
      Throwable targetEx = it.getTargetException();
      VertxAssert.handleThrowable(targetEx);
    } catch (Throwable t) {
      // Problem with invoking
      VertxAssert.handleThrowable(t);
    }
  }


}
