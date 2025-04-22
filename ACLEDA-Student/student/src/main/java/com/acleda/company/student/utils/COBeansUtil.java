package com.acleda.company.student.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public final class COBeansUtil {
    public COBeansUtil() {
    }

    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        PropertyDescriptor[] var7 = pds;
        int var6 = pds.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            PropertyDescriptor pd = var7[var5];
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return (String[])emptyNames.toArray(result);
    }
}
