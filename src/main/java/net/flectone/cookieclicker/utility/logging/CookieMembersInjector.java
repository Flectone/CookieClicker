package net.flectone.cookieclicker.utility.logging;

import com.google.inject.MembersInjector;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CookieMembersInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Logger logger;

    CookieMembersInjector(Logger logger, Field field) {
        this.logger = logger;
        this.field = field;
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T instance) {
        try {
            field.set(instance, logger);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
