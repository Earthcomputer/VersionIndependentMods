package net.earthcomputer.vimapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated class or member is not yet part of the stable
 * API and is subject to change
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface Incubating {

}
