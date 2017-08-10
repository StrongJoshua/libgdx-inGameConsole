/**
 *
 */

package com.strongjoshua.console;

/** Headless Console used for servers.
 *
 * @author Eric */
public class HeadlessConsole extends AbstractConsole {
	/** Creates an Headless console */
	public HeadlessConsole () {
		logToSystem = true;
	}
}
