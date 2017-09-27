/**
 *
 */

package com.strongjoshua.console.annotation;

import java.lang.annotation.*;

/** @author Eric Burns (ThaBalla1148) */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface HiddenCommand {

}
