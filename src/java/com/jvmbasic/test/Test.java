package com.jvmbasic.test;

import java.lang.annotation.*;

/**
 * Marks a method as a test method.
 *
 * Usage in JVM BASIC:
 *   #[Test "description"]
 *   sub testSomething()
 *       assert 1 + 1 = 2, "Math should work"
 *   end sub
 *
 * The compiler generates this annotation on the resulting Java method,
 * allowing the test runner to discover and execute tests via reflection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    /**
     * Optional description of the test.
     * Displayed in test output for clarity.
     */
    String value() default "";
}
