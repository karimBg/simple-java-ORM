package beanmanager;

import annotations.Inject;
import annotations.Provides;
import provider.H2ConnectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/*
    The Responsibility of this class is to get an instance of clss, check if clss contains fields annotated with: @Inject
    and find a way to construct the corresponding objects and inject them into those objects before providing them
    to the client code.
 */
public class BeanManager {

    private static BeanManager instance = new BeanManager();
    private Map<Class<?>, Supplier<?>> registry = new HashMap<>();

    // In the construction of the BeanManager object we need to check for all the classes and all their methods
    // and see if the methods contain the @Provides annotation
    private BeanManager() {
        // Create a list of all the classes inside our application
        // After searching, the best way is to use an external library like: Reflection
        // but for now let's create a simple list
        List<Class<?>> classes = List.of(H2ConnectionProvider.class);
        for(Class<?> clss: classes) {
            Method[] methods = clss.getDeclaredMethods();
            for(Method method: methods) {
                Provides providesAnnotation = method.getAnnotation(Provides.class);
                if(providesAnnotation != null) {
                    // The type of the method that it can create is it's return type
                    Class<?> returnType = method.getReturnType();
                    Supplier<?> supplier = () -> {
                        try {
                            if(!Modifier.isStatic(method.getModifiers())) {
                                Object obj = clss.getConstructor().newInstance();
                                return method.invoke(obj);
                            } else {
                                return method.invoke(null);
                            }
                        } catch (Exception e) {
                            // re-throw the exception into a runtimeException (we don't want to take care of it)
                            throw new RuntimeException(e);
                        }
                    };
                    // We provide inside the registry an association between the types we want to inject
                    // and a recipe to create instances of those types
                    registry.put(returnType, supplier);
                }
            }
        }
    }

    // We will use a naive implementation for the Singleton pattern(will be changed later)
    public static BeanManager getInstance() {
        return instance;
    }

    // We want to instantiate the clss class
    // and check for the fields annotated with: @Inject to put the right fields in
    public <T> T getInstance(Class<T> clss) {
        try {
            T t = clss.getConstructor().newInstance();

            // We want to return this: t object populated with the right value every time we see: @Inject
            // 1. get declared fields of t
            Field[] fields = clss.getDeclaredFields();
            for(Field field: fields) {
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                if(injectAnnotation != null) {
                    Class<?> injectedFieldType = field.getType();

                    // from the type we can get the supplier we put inside the registry:
                    Supplier<?> supplier = registry.get(injectedFieldType);

                    // We can now use the supplier to create an instance of this type
                    Object objectToInject = supplier.get();
                    field.setAccessible(true);
                    field.set(t, objectToInject);
                }
            }
            return t;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
