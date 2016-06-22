package net.earthcomputer.vimapi.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is used to change the return type or parameter type of a method,
 * using the obfuscation format (see
 * {@link net.earthcomputer.vimapi.core.tweaker.BytecodeTransformer#obfuscate(String)
 * BytecodeTransformer.obfuscate(String)}. When used on a method, it changes the
 * return type of the method, and when used on a parameter, it changes the
 * return type of that parameter.<br/>
 * <br/>
 * Due to how unsafe this is, methods containing the <code>ChangeType</code>
 * annotation may only be private, and other classes wishing to call such a
 * method must do so through {@link InlineOps}, when they can take into account
 * the dynamic method descriptor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface ChangeType {

	String value();

}
