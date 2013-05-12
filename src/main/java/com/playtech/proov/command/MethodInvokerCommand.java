package com.playtech.proov.command;

import java.util.Arrays;

public class MethodInvokerCommand implements Command {

    private String className;
    private String methodName;
    private Class[] argumentTypes;
    private Object[] arguments;
    private long transactionId;
    private String userName;

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public long getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Class[] getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }


    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void setArgumentTypes(Class... argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    @Override
    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "MethodInvokerCommand{" +
                "arguments=" + Arrays.toString(arguments) +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", argumentTypes=" + Arrays.toString(argumentTypes) +
                ", transactionId=" + transactionId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
