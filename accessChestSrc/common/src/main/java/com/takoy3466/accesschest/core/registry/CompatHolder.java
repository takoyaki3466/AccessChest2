package com.takoy3466.accesschest.core.registry;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Loader neutral reference to a registered object. The loader fills it once via {@link #set(Supplier)}.
 */
public final class CompatHolder<T> implements Supplier<T> {
    private final String id;
    private Supplier<? extends T> supplier;

    private CompatHolder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public static <T> CompatHolder<T> create(String id) {
        return new CompatHolder<>(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public T get() {
        if (supplier == null) {
            throw new IllegalStateException("Registry object '" + id + "' has not been registered yet.");
        }
        return supplier.get();
    }

    public void set(Supplier<? extends T> supplier) {
        if (this.supplier != null) {
            throw new IllegalStateException("Registry object '" + id + "' has already been registered.");
        }
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    @Override
    public String toString() {
        return "CompatHolder{" + id + '}';
    }
}
