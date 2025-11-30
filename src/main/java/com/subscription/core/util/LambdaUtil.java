package com.subscription.core.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for lambda-based operations
 */
public class LambdaUtil {

    public static <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (Objects.nonNull(value)) {
            setter.accept(value);
        }
    }

    public static <T> void updateIfNotNull(Supplier<T> valueSupplier, Consumer<T> setter) {
        T value = valueSupplier.get();
        if (Objects.nonNull(value)) {
            setter.accept(value);
        }
    }
}
