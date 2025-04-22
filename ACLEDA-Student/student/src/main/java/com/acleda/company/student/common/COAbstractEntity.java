package com.acleda.company.student.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

public abstract class  COAbstractEntity<T> implements Serializable, Cloneable, Comparable<T> {
    private static final long serialVersionUID = 4933913435290768430L;

    public abstract void init();

    public COAbstractEntity() {
        this.init();
    }

    public String toString() {
        try {
            return this.toJson();
        } catch (Exception var2) {
            return var2.getMessage();
        }
    }

    public void update(T entity) {
        try {
            BeanUtils.copyProperties(entity, this);
        } catch (Exception var3) {
        }

    }

    public String toXML() throws Exception {
        try {
            Class entityClass = this.getClass();
            JAXBContext context = JAXBContext.newInstance(new Class[]{entityClass});
            Marshaller m = context.createMarshaller();
            m.setProperty("jaxb.formatted.output", Boolean.TRUE);
            StringWriter writer = new StringWriter();
            m.marshal(this, writer);
            return writer.toString();
        } catch (Exception var5) {
            throw var5;
        }
    }

    public String toJson() throws Exception {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return objMapper.writeValueAsString(this);
        } catch (Exception var2) {
            throw var2;
        }
    }

    public void copyBeanProperties(Object target) {
        try {
            if (target != null) {
                Collection<String> excludes = new ArrayList();
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(this.getClass());
                PropertyDescriptor[] propertyDescriptorsTargets = BeanUtils.getPropertyDescriptors(target.getClass());
                PropertyDescriptor[] var8 = propertyDescriptors;
                int var7 = propertyDescriptors.length;

                for(int var6 = 0; var6 < var7; ++var6) {
                    PropertyDescriptor propertyDescriptor = var8[var6];
                    String propName = propertyDescriptor.getName();
                    boolean bPropNameFound = false;
                    PropertyDescriptor[] var14 = propertyDescriptorsTargets;
                    int var13 = propertyDescriptorsTargets.length;

                    for(int var12 = 0; var12 < var13; ++var12) {
                        PropertyDescriptor propertyDescriptorTarget = var14[var12];
                        String propNameTarget = propertyDescriptorTarget.getName();
                        if (propName.equalsIgnoreCase(propNameTarget)) {
                            bPropNameFound = true;
                            break;
                        }
                    }

                    if (!bPropNameFound) {
                        excludes.add(propName);
                    }
                }

                BeanUtils.copyProperties(this, target, (String[])excludes.toArray(new String[excludes.size()]));
            }
        } catch (Exception var16) {
            throw var16;
        }
    }
}