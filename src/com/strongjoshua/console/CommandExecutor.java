/**
 * Copyright 2015 StrongJoshua (swampert_555@yahoo.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.strongjoshua.console;

/**
 * Extend this class and fill it with methods that you wish to have work with the {@link Console}. Then call
 * {@link Console#setCommandExecutor(CommandExecutor)}.<br>
 * It would be best if all methods were static and this class had no constructor, but the console should still work correctly with instance
 * methods.<br>
 * <br>
 * <b>Notes</b><br>
 * <ul>
 * <li>Arguments <i><b>must</b></i> be primitive types (the only exception being {@link String}).</li>
 * <li>No two methods, of the same name, can have the same number of parameters with interchangeable doubles or floats (i.e.
 * someMethod(float, float) and someMethod(double, double) will not work). <b>As of right now, all methods are assumed to be using floats.</b></li>
 * <li>When calling the methods in the {@link Console} you must include a decimal <code>'.'</code> for the number to count as a float, and not an integer.</li>
 * </ul>
 * 
 * @author StrongJoshua
 */
public abstract class CommandExecutor {}