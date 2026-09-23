package uz.mirix.queryguard.jdbc;

import uz.mirix.queryguard.Fingerprints;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Map;

final class BindingFingerprint {
    private BindingFingerprint() {}
    static String of(Map<Integer, Object> bindings) {
        if (bindings.isEmpty()) return "";
        StringBuilder value = new StringBuilder();
        bindings.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(entry -> value.append(entry.getKey()).append(':').append(type(entry.getValue())).append(':').append(safeHash(entry.getValue())).append(';'));
        return Fingerprints.sha256(value.toString());
    }
    private static String type(Object value) { return value == null ? "null" : value.getClass().getName(); }
    private static int safeHash(Object value) {
        if (value == null) return 0;
        Class<?> type = value.getClass(); if (!type.isArray()) return value.hashCode();
        int length = Array.getLength(value), hash = 1;
        for (int i = 0; i < length; i++) { Object element = Array.get(value, i); hash = 31 * hash + (element == null ? 0 : element.hashCode()); }
        return hash;
    }
}
