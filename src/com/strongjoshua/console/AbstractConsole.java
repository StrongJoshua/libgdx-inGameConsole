/**
 *
 */

package com.strongjoshua.console;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.strongjoshua.console.annotation.ConsoleDoc;

/** @author Eric */
public abstract class AbstractConsole implements Console, Disposable {
	protected CommandExecutor exec;

	protected final Log log;
	protected boolean logToSystem;

	protected boolean disabled;

	protected boolean executeHiddenCommands = true;
	protected boolean displayHiddenCommands = false;
	protected boolean consoleTrace = false;

	public AbstractConsole () {
		log = new Log();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#setLoggingToSystem(java.lang.Boolean)
	 */
	@Override
	public void setLoggingToSystem (Boolean log) {
		this.logToSystem = log;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#log(java.lang.String, com.strongjoshua.console.GUIConsole.LogLevel)
	 */
	@Override
	public void log (String msg, LogLevel level) {
		log.addEntry(msg, level);

		if (logToSystem) {
			switch (level) {
			case ERROR:
				System.err.println("> " + msg);
				break;
			default:
				System.out.println("> " + msg);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#log(java.lang.String)
	 */
	@Override
	public void log (String msg) {
		this.log(msg, LogLevel.DEFAULT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#printLogToFile(java.lang.String)
	 */
	@Override
	public void printLogToFile (String file) {
		this.printLogToFile(Gdx.files.local(file));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#printLogToFile(com.badlogic.gdx.files.FileHandle)
	 */
	@Override
	public void printLogToFile (FileHandle fh) {
		if (log.printToFile(fh)) {
			log("Successfully wrote logs to file.", LogLevel.SUCCESS);
		} else {
			log("Unable to write logs to file.", LogLevel.ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#isDisabled()
	 */
	@Override
	public boolean isDisabled () {
		return disabled;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#setDisabled(boolean)
	 */
	@Override
	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#setCommandExecutor(com.strongjoshua.console.CommandExecutor)
	 */
	@Override
	public void setCommandExecutor (CommandExecutor commandExec) {
		exec = commandExec;
		exec.setConsole(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#execCommand(java.lang.String)
	 */
	@Override
	public void execCommand (String command) {
		if (disabled) return;

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

		Class<? extends CommandExecutor> clazz = exec.getClass();
		Method[] methods = ClassReflection.getMethods(clazz);
		Array<Integer> possible = new Array<Integer>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equalsIgnoreCase(methodName) && ConsoleUtils.canExecuteCommand(this, method)) {
				possible.add(i);
			}
		}

		if (possible.size <= 0) {
			log("No such method found.", LogLevel.ERROR);
			return;
		}

		int size = possible.size;
		int numArgs = sArgs == null ? 0 : sArgs.length;
		for (int i = 0; i < size; i++) {
			Method m = methods[possible.get(i)];
			Class<?>[] params = m.getParameterTypes();
			if (numArgs == params.length) {
				try {
					Object[] args = null;

					try {
						if (sArgs != null) {
							args = new Object[numArgs];

							for (int j = 0; j < params.length; j++) {
								Class<?> param = params[j];
								final String value = sArgs[j];

								if (param.equals(String.class)) {
									args[j] = value;
								} else if (param.equals(Boolean.class) || param.equals(boolean.class)) {
									args[j] = Boolean.parseBoolean(value);
								} else if (param.equals(Byte.class) || param.equals(byte.class)) {
									args[j] = Byte.parseByte(value);
								} else if (param.equals(Short.class) || param.equals(short.class)) {
									args[j] = Short.parseShort(value);
								} else if (param.equals(Integer.class) || param.equals(int.class)) {
									args[j] = Integer.parseInt(value);
								} else if (param.equals(Long.class) || param.equals(long.class)) {
									args[j] = Long.parseLong(value);
								} else if (param.equals(Float.class) || param.equals(float.class)) {
									args[j] = Float.parseFloat(value);
								} else if (param.equals(Double.class) || param.equals(double.class)) {
									args[j] = Double.parseDouble(value);
								}
							}
						}
					} catch (Exception e) {
						// Error occurred trying to parse parameter, continue to next function
						continue;
					}

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
					if (consoleTrace) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						log(sw.toString(), LogLevel.ERROR);
					}
					return;
				}
			}
		}

		log("Bad parameters. Check your code.", LogLevel.ERROR);
	}

	private Method[] getAllMethods() {
		Method[] pMethods = ClassReflection.getDeclaredMethods(exec.getClass().getSuperclass());
		Method[] mMethods = ClassReflection.getDeclaredMethods(exec.getClass());
		Method[] methods = new Method[pMethods.length + mMethods.length];
		for (int i = 0; i < pMethods.length; i++)
			methods[i] = pMethods[i];
		for (int i = 0; i < mMethods.length; i++)
			methods[i + pMethods.length] = mMethods[i];
		return methods;
	}

	@Override
	public void printCommands () {
		Method[] methods = getAllMethods();
		for (Method m : methods) {
			if (m.isPublic() && ConsoleUtils.canDisplayCommand(this, m)) {
				String s = "";
				s += m.getName();
				s += " : ";

				Class<?>[] params = m.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					s += params[i].getSimpleName();
					if (i < params.length - 1) {
						s += ", ";
					}
				}

				log(s);
			}
		}
	}

	@Override
	public void printHelp(String command) {
		Method[] methods = getAllMethods();
		boolean found = false;
		for (Method m : methods) {
			if(m.getName().equals(command)) {
				found = true;
				StringBuilder sb = new StringBuilder();
				sb.append(m.getName()).append(": ");
				Annotation annotation = m.getDeclaredAnnotation(ConsoleDoc.class);
				if(annotation != null) {
					ConsoleDoc doc = annotation.getAnnotation(ConsoleDoc.class);
					sb.append(doc.description()).append("\n");
					Class<?>[] params = m.getParameterTypes();
					for(int i = 0; i < params.length; i++) {
						for(int j = 0; j < m.getName().length() + 2; j++)
							// using spaces this way works with monotype fonts
							sb.append(" ");
						sb.append(params[i].getSimpleName()).append(": ");
						if(i < doc.paramDescriptions().length)
							sb.append(doc.paramDescriptions()[i]);
						if(i < params.length - 1)
							sb.append("\n");
					}
				} else {
					Class<?>[] params = m.getParameterTypes();
					for (int i = 0; i < params.length; i++) {
						sb.append(params[i].getSimpleName());
						if (i < params.length - 1) {
							sb.append(", ");
						}
					}
				}

				log(sb.toString());
			}
		}

		if(!found)
			log("Command does not exist.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#setExecuteHiddenCommands(boolean)
	 */
	@Override
	public void setExecuteHiddenCommands (boolean enabled) {
		executeHiddenCommands = enabled;
	}

	@Override
	public boolean isExecuteHiddenCommandsEnabled () {
		return executeHiddenCommands;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.strongjoshua.console.Console#setDisplayHiddenCommands(boolean)
	 */
	@Override
	public void setDisplayHiddenCommands (boolean enabled) {
		displayHiddenCommands = enabled;
	}

	@Override
	public boolean isDisplayHiddenCommandsEnabled () {
		return displayHiddenCommands;
	}

	@Override
	public void setConsoleStackTrace (boolean enabled) {
		this.consoleTrace = enabled;
	}
}
