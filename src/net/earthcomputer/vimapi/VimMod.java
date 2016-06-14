package net.earthcomputer.vimapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which must be put on a class if it is to be recognized as a VIM
 * mod.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VimMod {

	String id();

	String name() default "";

	String version();

	String minimumMCVersion() default "";

}
