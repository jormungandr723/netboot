package com.dcis.netboot.nettyconfig;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyMapping {
     String value()  default "";
}
