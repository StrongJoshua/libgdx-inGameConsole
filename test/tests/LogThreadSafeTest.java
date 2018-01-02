package tests;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.math.MathUtils;
import com.strongjoshua.console.Console;

public class LogThreadSafeTest {
	private static class LogTester implements Callable<LogTester> {
		private int id = -1;
		private long wait = -1;
		private Console console;

		public LogTester(int id, long wait, Console console) {
			this.id = id;
			this.wait = wait;
			this.console = console;
		}

		@Override
		public LogTester call () throws Exception {
			long startTime = Calendar.getInstance().getTimeInMillis();
			while ((startTime + wait) >= Calendar.getInstance().getTimeInMillis()) {
				int sleep = MathUtils.random(0, 1000);
				Thread.sleep(sleep);
				console.log("test " + id);
			}
			return this;
		}
	}

	public static void create (Console console, long wait) {
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 0; i < 16; i++) {
			executor.submit(new LogTester(i, wait, console));
		}
	}
}
