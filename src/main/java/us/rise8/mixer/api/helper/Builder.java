package us.rise8.mixer.api.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;


/**
 * Utility which allows for constructing instances
 * of a class via its setters. Example:
 * <pre>
 * Builder.build(Product.class)
 *     .with(p -> p.setId(123))
 *     .with(p -> p.setName('MyName')).get();
 * </pre>
 */

@Slf4j
public class Builder<T> {
    private T instance;


    /**
     * Constructor called by static build method.
     *
     * @param clazz The class to build
     */
    private Builder(Class<T> clazz) {
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (
                InstantiationException |
                        InvocationTargetException |
                        NoSuchMethodException |
                        IllegalAccessException e
        ) {
            log.error(e.getMessage());
        }
    }

    /**
     * Specify the class that should be built
     *
     * @param class The class type to construct
     * @return A Builder instance for the class
     */
    public static <T> Builder<T> build(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    /**
     * Applies a method to the instance being built
     *
     * @param setter ex. <code>p -> p.setId(123)</code>
     * @return this
     */
    public Builder<T> with(Consumer<T> setter) {
        setter.accept(instance);
        return this;
    }

    /**
     * Gets the constructed instance with all
     * setters applied
     *
     * @return The instance which was built
     */
    public T get() {
        return this.instance;
    }
}
