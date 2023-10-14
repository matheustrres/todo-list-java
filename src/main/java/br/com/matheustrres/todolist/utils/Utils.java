package br.com.matheustrres.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);

        PropertyDescriptor[] propertyDescriptors = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor pd : propertyDescriptors) {
            String propertyName = pd.getName();

            Object srcValue = src.getPropertyValue(propertyName);

            if (srcValue == null) {
                emptyNames.add(propertyName);
            }
        }

        String[] result = new String[emptyNames.size()];

        return emptyNames.toArray(result);
    }
}
