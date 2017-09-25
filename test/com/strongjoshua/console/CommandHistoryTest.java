
package com.strongjoshua.console;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandHistoryTest {
	private CommandHistory history;

	@Before
	public void setUp () throws Exception {
		history = new CommandHistory();
	}

	@Test
	public void shouldCycleFromBeginningIfPreviousCommandIsUsedAtTheEndOfHistory () {
		history.store("1");
		history.store("2");

		history.getPreviousCommand();
		history.getPreviousCommand();

		assertEquals("2", history.getPreviousCommand());
	}

	@Test
	public void shouldOnlyStoreACommandOnceIfItIsRanMultipleTimesRightAfterEachOther () {
		history.store("1");
		history.store("2");
		history.store("2");

		assertEquals("2", history.getPreviousCommand());
		assertEquals("1", history.getPreviousCommand());
	}

	@Test
	public void shouldResetTheHistoryPositionWhenANewCommandIsStored () {
		history.store("command 1");
		history.store("command 2");
		history.store("command 3");
		history.getPreviousCommand();

		history.store("command 4");
		assertEquals("command 4", history.getPreviousCommand());
	}

	@Test
	public void shouldAllowGoingOverTheCompleteCommandHistory () {
		for (int i = 0; i < 5; i++) {
			history.store("command " + i);
		}

		for (int i = 4; i >= 0; i--) {
			assertEquals("command " + i, history.getPreviousCommand());
		}

		for (int i = 1; i < 5; i++) {
			assertEquals("command " + i, history.getNextCommand());
		}
	}

	@Test
	public void shouldNotReturnTheLastStoredCommandWhenGoingToTheNextCommand () {
		history.store("command");

		assertEquals("", history.getNextCommand());
	}

	@Test
	public void shouldNotReturnThePreviousCommandWhenGoingToTheNextCommand () {
		history.store("command 2");

		assertEquals("command 2", history.getPreviousCommand());
		assertEquals("", history.getNextCommand());
	}

	@Test
	public void shouldReturnNothingWhenTryingToLookAtFutureCommandsThatDoNotExist () {
		history.store("command");
		assertEquals("", history.getNextCommand());
		assertEquals("", history.getNextCommand());
	}

	@Test
	public void shouldAllowMeToFindMyPreviousCommand () {
		history.store("command");

		assertEquals("command", history.getPreviousCommand());
	}

	@Test
	public void shouldReturnTheLastCommandRunWhenThereAreNoMorePastCommands () {
		history.store("command");
		history.getPreviousCommand();

		assertEquals("command", history.getPreviousCommand());
	}

	@Test
	public void shouldReturnNothingWhenWeHaveNoHistory () {
		assertEquals("", history.getPreviousCommand());
		assertEquals("", history.getNextCommand());
	}
}
