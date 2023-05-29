package tr.com.injection;

public interface ApplicationContext {
    Object getBeanByClass(Class<?> classz);
    void displayAllBeans();
}
