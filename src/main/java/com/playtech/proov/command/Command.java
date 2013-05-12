package com.playtech.proov.command;


import java.io.Serializable;

/**
 * Command to invoke remote service.
 */
public interface Command extends Serializable {

    public long getTransactionId();

    public String getUserName();

    public String getClassName();

    public Class[] getArgumentTypes();

    public Object[] getArguments();

    String getMethodName();


    void setMethodName(String methodName);

    void setClassName(String className);

    void setArgumentTypes(Class... argumentTypes);

    void setArguments(Object... arguments);

    void setTransactionId(long transactionId);

    void setUserName(String userName);
}
