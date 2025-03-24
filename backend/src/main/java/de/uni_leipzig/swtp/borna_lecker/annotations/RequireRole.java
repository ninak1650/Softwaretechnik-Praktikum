package de.uni_leipzig.swtp.borna_lecker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireRole {
    Rolle[] value();
}