package com.acleda.company.student.common;

import com.acleda.company.student.utils.COBeansUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDTO<D, E> extends COAbstractEntity<D> {
    @Serial
    private static final long serialVersionUID = 2161178260327301388L;
    protected static Logger log = LoggerFactory.getLogger(COAbstractEntity.class);
    public AbstractDTO() {
    }

    public AbstractDTO(E entity) {
        this.fromEntity(entity);
    }

    public E toEntity(E entity) {
        try {
            if (entity != null) {
                BeanUtils.copyProperties(this, entity, COBeansUtil.getNullPropertyNames(this));
            }
        } catch (Exception var3) {
            log.error("toEntity");
        }

        return entity;
    }

    public void fromEntity(E entity) {
        if (entity != null) {
            BeanUtils.copyProperties(entity, this, COBeansUtil.getNullPropertyNames(entity));
        }

    }

    public Set<ConstraintViolation<D>> validate(D dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(dto, new Class[0]);
    }


    public static String[] getNullOrIgnoredProperties(Object source, String... ignoredProperties) {
        Set<String> nullProps = new HashSet<>(Arrays.asList(COBeansUtil.getNullPropertyNames(source)));
        if (ignoredProperties != null) {
            nullProps.addAll(Arrays.asList(ignoredProperties));
        }
        return nullProps.toArray(new String[0]);
    }

}
