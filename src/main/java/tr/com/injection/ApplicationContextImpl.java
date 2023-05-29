package tr.com.injection;

import org.reflections.Reflections;
import tr.com.injection.annotations.Bean;
import tr.com.injection.annotations.Init;
import tr.com.injection.annotations.Inject;

import javax.management.RuntimeErrorException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {
    private Map<Class<?>, Object> instantiatedBeans;
    private Map<Class<?>, Class<?>> diMap;

    public ApplicationContextImpl() {
        diMap = new HashMap<>();
        instantiatedBeans = new HashMap<>();
        scanBeans();
    }

    @Override
    public Object getBeanByClass(Class<?> beanClass) {
        Object bean = instantiatedBeans.get(beanClass);
        if (bean == null) {
            throw new RuntimeException("Bean " + beanClass + " not found");
        }
        return bean;
    }

    @Override
    public void displayAllBeans() {
        System.out.println("-Bean List-");
        for (var entry : instantiatedBeans.entrySet()) {
            System.out.println("Name: " + entry.getKey().getSimpleName() + ", Type: " + entry.getValue().getClass().getName());
        }
    }

    private void scanBeans() {
        fillDependencyMap();
        for (Class<?> beanClass : getAllClassesWithAnnotation(getClass().getPackageName(), Bean.class)) {
            List<String> instanceDependencyList = new ArrayList<>();
            Object classInstance = createBeanInstance(beanClass);
            instanceDependencyList.add(classInstance.getClass().getName());
            inject(classInstance, instanceDependencyList);
        }
    }

    private void fillDependencyMap() {
        for (Class<?> beanClass : getAllClassesWithAnnotation(getClass().getPackageName(), Bean.class)) {
            Class<?>[] interfaces = beanClass.getInterfaces();
            if (interfaces.length == 0) {
                diMap.put(beanClass, beanClass);
            } else {
                for (Class<?> item : interfaces) {
                    diMap.put(beanClass, item);
                }
            }
        }
    }

    private void inject(Object beanInstance, List<String> instanceDependencyList) {
        for (Field field : beanInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object fieldInstance = getBeanInstance(field.getType());
                try {
                    field.set(beanInstance, fieldInstance);
                    checkCircularDependency(instanceDependencyList, fieldInstance);
                    instanceDependencyList.add(fieldInstance.getClass().getName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                inject(fieldInstance, instanceDependencyList);
            }
        }

    }

    private void checkCircularDependency(List<String> instanceDependencyList, Object field) {
        Class<?> injectedBeanClass = field.getClass();
        String injectedBeanName = injectedBeanClass.getName();

        if (instanceDependencyList.contains(injectedBeanName)) {
            instanceDependencyList.add(injectedBeanName);
            throw new RuntimeException("\nApplication Context Failed: Circular Dependency\n[" + instanceDependencyList.stream()
                    .collect(Collectors.joining("->")) + "]");
        }
    }

    public <T> Object getBeanInstance(Class<T> interfaceClass) {
        Class<?> implementationClass = getImplimentationClass(interfaceClass);

        if (instantiatedBeans.containsKey(implementationClass)) {
            return instantiatedBeans.get(implementationClass);
        }

        synchronized (instantiatedBeans) {
            return createBeanInstance(implementationClass);
        }
    }

    private Class<?> getImplimentationClass(Class<?> interfaceClass) {
        Set<Map.Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";

        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Map.Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }

    private Object createBeanInstance(Class<?> beanClass) {
        Object beanInstance = null;
        try {
            beanInstance = beanClass.newInstance();
            processInit(beanInstance);
            instantiatedBeans.put(beanClass, beanInstance);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return beanInstance;
    }

    private void processInit(Object obj) throws Exception {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Init.class)) {
                method.invoke(obj);
            }
        }
    }

    private <T> List<Class<? extends T>> getAllClassesWithAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Set<Class<? extends T>> classes = new HashSet<>();
        Reflections reflections = new Reflections(packageName);
        for (Class<?> beanClass : reflections.getTypesAnnotatedWith(annotation)) {
            classes.add((Class<? extends T>) beanClass);
        }
        return classes.stream().sorted(Comparator.comparing(Class::getSimpleName)).collect(Collectors.toList());
    }


}