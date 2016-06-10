/**
 * 
 */
package com.strongjoshua.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * @author Eric
 *
 */
public abstract class AbstractConsole implements Console, Disposable {
	protected CommandExecutor exec;
	
	protected final Log log;
	protected boolean logToSystem;
	
	protected boolean disabled;
	
	public AbstractConsole() {
		log = new Log();
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#setLoggingToSystem(java.lang.Boolean)
	 */
	@Override
	public void setLoggingToSystem (Boolean log) {
		this.logToSystem = log;
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#log(java.lang.String, com.strongjoshua.console.GUIConsole.LogLevel)
	 */
	@Override
	public void log (String msg, LogLevel level) {
		log.addEntry(msg, level);

		if (logToSystem) {
			switch(level) {
				case ERROR:
					System.err.println("> " + msg);
					break;
				default:
					System.out.println("> " + msg);
					break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#log(java.lang.String)
	 */
	@Override
	public void log (String msg) {
		this.log(msg, LogLevel.DEFAULT);
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#printLogToFile(java.lang.String)
	 */
	@Override
	public void printLogToFile (String file) {
		this.printLogToFile(Gdx.files.local(file));
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#printLogToFile(com.badlogic.gdx.files.FileHandle)
	 */
	@Override
	public void printLogToFile (FileHandle fh) {
		if (log.printToFile(fh))
			log("Successfully wrote logs to file.", LogLevel.SUCCESS);
		else
			log("Unable to write logs to file.", LogLevel.ERROR);
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#isDisabled()
	 */
	@Override
	public boolean isDisabled () {
		return disabled;
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#setDisabled(boolean)
	 */
	@Override
	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#setCommandExecutor(com.strongjoshua.console.CommandExecutor)
	 */
	@Override
	public void setCommandExecutor (CommandExecutor commandExec) {
		exec = commandExec;
		exec.setConsole(this);
	}

	/* (non-Javadoc)
	 * @see com.strongjoshua.console.Console#execCommand(java.lang.String)
	 */
	@Override
	public void execCommand(String command) {
		log(command, LogLevel.COMMAND);

		String[] parts = command.split(" ");
		String methodName = parts[0];
		String[] sArgs = null;
		if (parts.length > 1) {
			sArgs = new String[parts.length - 1];
			for (int i = 1; i < parts.length; i++) {
				sArgs[i - 1] = parts[i];
			}
		}

		// attempt to convert arguments to numbers. If the conversion does not work, keep the argument as a string.
		Object[] args = null;
		if (sArgs != null) {
			args = new Object[sArgs.length];
			for (int i = 0; i < sArgs.length; i++) {
				String s = sArgs[i];
				try {
					int j = Integer.parseInt(s);
					args[i] = j;
				} catch (NumberFormatException e) {
					try {
						float f = Float.parseFloat(s);
						args[i] = f;
					} catch (NumberFormatException e2) {
						args[i] = s;
					}
				}
			}
		}

		Class<? extends CommandExecutor> clazz = exec.getClass();
		Method[] methods = ClassReflection.getMethods(clazz);
		Array<Integer> possible = new Array<Integer>();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equalsIgnoreCase(methodName)) possible.add(i);
		}
		if (possible.size <= 0) {
			log("No such method found.", LogLevel.ERROR);
			return;
		}
		int size = possible.size;
		int numArgs;
		numArgs = args == null ? 0 : args.length;
		for (int i = 0; i < size; i++) {
			Method m = methods[possible.get(i)];
			Class<?>[] params = m.getParameterTypes();
			if (numArgs != params.length)
				continue;
			else {
				try {
					m.setAccessible(true);
					m.invoke(exec, args);
					return;
				} catch (ReflectionException e) {
					String msg = e.getMessage();
					if (msg == null || msg.length() <= 0 || msg.equals("")) {
						msg = "Unknown Error";
						e.printStackTrace();
					}
					log(msg, LogLevel.ERROR);
					return;
				}
			}
		}
		log("Bad parameters. Check your code.", LogLevel.ERROR);

	}
}
