/**
 *
 */

package com.strongjoshua.console;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Eric
 */
public class ConsoleTest {

	private Console headlessConsole;
	private CommandExecutor commandExec;
	private boolean commandFound;

	@Before public void setup () {
		commandFound = false;
		headlessConsole = new HeadlessConsole();
		commandExec = new CommandExecutor() {

			public void testString (String str) {
				commandFound = true;

				console.log(str);
			}

			public void testBoolean (Boolean arg1, boolean arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testByte (Byte arg1, byte arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testShort (Short arg1, short arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testInteger (Integer arg1, int arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testLong (Long arg1, long arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testFloat (Float arg1, float arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testDouble (Double arg1, double arg2) {
				commandFound = true;

				console.log(String.valueOf(arg1));
				console.log(String.valueOf(arg2));
			}

			public void testObject (Object arg1) {
				commandFound = true;

				console.log(String.valueOf(arg1));
			}
		};

		headlessConsole.setCommandExecutor(commandExec);
	}

	@Test public void test_StringArgument () {
		headlessConsole.execCommand("testString STRING");

		assertTrue(commandFound);
	}

	@Test public void test_BooleanArgument () {
		headlessConsole.execCommand("testBoolean true false");

		assertTrue(commandFound);
	}

	@Test public void test_ByteArgument () {
		headlessConsole.execCommand("testByte " + Byte.MAX_VALUE + " " + Byte.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_ShortArgument () {
		headlessConsole.execCommand("testShort " + Short.MAX_VALUE + " " + Short.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_IntegerArgument () {
		headlessConsole.execCommand("testInteger " + Integer.MAX_VALUE + " " + Integer.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_LongArgument () {
		headlessConsole.execCommand("testLong " + Long.MAX_VALUE + " " + Long.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_FloatArgument () {
		headlessConsole.execCommand("testFloat " + Float.MAX_VALUE + " " + Float.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_DoubleArgument () {
		headlessConsole.execCommand("testDouble " + Double.MAX_VALUE + " " + Double.MIN_VALUE);

		assertTrue(commandFound);
	}

	@Test public void test_ObjectArgument () {
		headlessConsole.execCommand("testObject test test");

		assertFalse(commandFound);
	}

}
