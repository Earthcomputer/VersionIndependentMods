package net.earthcomputer.vimapi.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method which is written in bytecode rather than in Java by use of
 * the {@link Bytecode} class. Any Java code in the annotated method which is
 * not a static call to the <code>Bytecode</code> class will be deleted at
 * runtime. The {@link ContainsInlineBytecode} technique should be used in
 * preference to this one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BytecodeMethod {

}
