package com.forter.contracts;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a new object and fills Optional.absent()
 */
public class ContractFactory<T> {

    private final Kryo kryo;
    private final T newInstance;
    public ContractFactory(Class<T> clazz) {
        this.kryo = new Kryo();
        kryo.register(clazz, new FieldSerializer(kryo, clazz));
        newInstance = createAndInitializeInstance(clazz);
    }

    public T newInstance() {
        return kryo.copy(newInstance);
    }
    private <T> T createAndInitializeInstance(Class<T> clazz) {
        T instance = createInstance(clazz);
        initializeOptionalAsAbsent(instance);
        return instance;
    }

    private static <T> void initializeOptionalAsAbsent(T instance) {
        Class<?> clazz = instance.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                Class fieldType = field.getType();
                if (Optional.class.isAssignableFrom(fieldType)) {
                    try {
                        field.set(instance, Optional.absent());
                    } catch (IllegalAccessException e) {
                        throw Throwables.propagate(e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException | NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }
}
