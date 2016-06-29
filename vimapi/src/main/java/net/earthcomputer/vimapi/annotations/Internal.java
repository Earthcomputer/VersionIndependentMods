package net.earthcomputer.vimapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies internal methods/fields/packages which modders should not use
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.PACKAGE, ElementType.METHOD, ElementType.FIELD })
public @interface Internal {

}
