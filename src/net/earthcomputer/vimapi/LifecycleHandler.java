package net.earthcomputer.vimapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which goes in the main mod class which flags a method as being
 * an event handler for the specified lifecycle event. The method must have no
 * parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LifecycleHandler {

	LifecycleEventType value();

}
