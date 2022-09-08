package com.deptagency.sqlexplain;

public class PreparedStetementValue {

    /**
     * Value itself.
     */
    private Object value;
    private Class<?> parameterType;

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public PreparedStetementValue(Object valueToSet, Class<?> parameterType) {
        this();
        this.value = valueToSet;
        this.parameterType = parameterType;
    }

    public PreparedStetementValue() {
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
