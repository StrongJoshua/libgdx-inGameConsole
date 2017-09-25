
package com.strongjoshua.console.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleDoc {
	String description() default "";

	/** Put these in the same order as your actual function parameters.
	 *
	 * @return An array of parameter descriptions. */
	String[] paramDescriptions() default {};
}
