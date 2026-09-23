package uz.mirix.queryguard.jdbc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
final class ReflectionSupport {
    private ReflectionSupport() {}
    static Object invoke(Method method, Object target, Object[] args) throws Throwable {
        try { return method.invoke(target, args); } catch (InvocationTargetException ex) { throw ex.getTargetException(); }
    }
}
