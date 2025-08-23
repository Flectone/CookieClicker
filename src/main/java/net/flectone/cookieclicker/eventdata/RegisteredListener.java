package net.flectone.cookieclicker.eventdata;

import lombok.Getter;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@Getter
public class RegisteredListener {

    private final CookieListener listener;
    private final MethodHandle methodHandle;

    public RegisteredListener(CookieListener listener, Method method) throws IllegalAccessException {
        this.listener = listener;

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        this.methodHandle = lookup.unreflect(method);
    }

    public void invoke(BasePlayerEvent event) throws Throwable {
        methodHandle.invoke(listener, event);
    }
}
