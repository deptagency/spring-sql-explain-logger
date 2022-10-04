package com.deptagency.sqlexplain;

public class PreparedStatementValue {

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

    public PreparedStatementValue(Object valueToSet, Class<?> parameterType) {
        this.value = valueToSet;
        this.parameterType = parameterType;
    }

    public PreparedStatementValue() {
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
