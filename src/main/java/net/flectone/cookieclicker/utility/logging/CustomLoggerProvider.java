package net.flectone.cookieclicker.utility.logging;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class CustomLoggerProvider implements TypeListener {

    private final Logger logger;

    public CustomLoggerProvider(Logger logger) {
        this.logger = logger;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    encounter.register(new CookieMembersInjector<>(logger, field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
