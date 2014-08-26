package com.forter.contracts.reflection;

import com.forter.contracts.IContractsBolt;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Reflects on types and annotations of {@link com.forter.contracts.IContractsBolt} implementations.
 */
public class ContractBoltReflector<TInput, TOutput, TContractsBolt extends IContractsBolt<TInput, TOutput>> {

    private final TContractsBolt bolt;

    public enum EXECUTE_RETURN_TYPE {NOT_NULL_CONTRACT, OPTIONAL_CONTRACT, COLLECTION_CONTRACTS};
    private EXECUTE_RETURN_TYPE executeReturnType;
    private Class<TInput> inputClass;

    public ContractBoltReflector(TContractsBolt bolt) {
        this.bolt = bolt;
        reflectOnDelegate();
    }

    public EXECUTE_RETURN_TYPE getExecuteReturnType() {
        return executeReturnType;
    }

    public Class<TInput> getInputClass() {
        return inputClass;
    }

    private void reflectOnDelegate() {
        for (Method method : bolt.getClass().getDeclaredMethods()) {
            if (method.getName().equals("executeValidInput") && !method.isBridge()) {
                Invokable<?, Object> invokable = Invokable.from(method);
                if (invokable.isPublic() && !invokable.isStatic()) {
                    TypeToken<?> parameterType = invokable.getParameters().get(0).getType();
                    inputClass = (Class<TInput>) parameterType.getRawType();
                    checkAnnotations(inputClass);
                    TypeToken<?> returnType = invokable.getReturnType();
                    Class<?> returnClass = returnType.getRawType();
                    checkAnnotations(returnClass);
                    if (Collection.class.isAssignableFrom(returnClass)) {
                        executeReturnType = EXECUTE_RETURN_TYPE.COLLECTION_CONTRACTS;
                    }
                    else if (Optional.class.isAssignableFrom(returnClass)) {
                        executeReturnType = EXECUTE_RETURN_TYPE.OPTIONAL_CONTRACT;
                    }
                    else {
                        executeReturnType = EXECUTE_RETURN_TYPE.NOT_NULL_CONTRACT;
                    }
                    break;
                }
            }
        }
        Preconditions.checkState(inputClass != null);
    }

    private void checkAnnotations(Class<?> contractClass) {
        Field[] fields = contractClass.getFields();
        for (Field field : fields) {
            Class fieldType = field.getType();
            if (Optional.class.isAssignableFrom(fieldType)) {
                checkAnnotation(contractClass, field,UnwrapValidatedValue.class);
                checkNoAnnotation(contractClass, field, NotNull.class);
            }
            else {
                checkNoAnnotation(contractClass, field, UnwrapValidatedValue.class);
                checkAnnotation(contractClass, field,NotNull.class);
            }
        }
    }

    private void checkAnnotation(Class<?> contractClass, Field field, Class<? extends Annotation> annotationClass) {
        boolean foundAnnotation = !Iterables.isEmpty(getAnnotationsOfClass(field, annotationClass));
        Preconditions.checkState(foundAnnotation, "Field " + field.getType().getName() + " " + field.getName() + " in class " + contractClass.getName() + " must be annotated with @" + annotationClass.getCanonicalName());
    }

    private void checkNoAnnotation(Class<?> contractClass, Field field, Class<? extends Annotation> annotationClass) {
        boolean noAnnotation = Iterables.isEmpty(getAnnotationsOfClass(field, annotationClass));
        Preconditions.checkState(noAnnotation, "Field " + field.getType().getName() + " " + field.getName() + " in class " + contractClass.getName() + " must not be annotated with @" + annotationClass.getCanonicalName());
    }

    private Iterable<? extends Annotation> getAnnotationsOfClass(Field field, Class<? extends Annotation> annotationClass) {
        return Iterables.filter(Arrays.asList(field.getDeclaredAnnotations()), annotationClass);
    }

}
