package attributes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Host
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {
    String name() default "";
}
