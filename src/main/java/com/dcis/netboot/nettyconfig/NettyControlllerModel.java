package com.dcis.netboot.nettyconfig;

import java.lang.reflect.Method;

public class NettyControlllerModel {

  private Object obj;
  private Method method;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
