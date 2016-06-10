package net.earthcomputer.vimapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VimMod {

	String id();
	String name() default "";
	String version();
	String minimumMCVersion() default "";
	
}
