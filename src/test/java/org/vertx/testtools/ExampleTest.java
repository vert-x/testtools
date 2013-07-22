/**
 * 
 */
package org.vertx.testtools;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author richard
 */
@RunWith(JavaClassRunner.class)
public class ExampleTest extends TestVerticle {

	@Test
	@Ignore
	public void ignoresAreIgnored() {
		VertxAssert.fail("This test should be ignored");
	}

}
