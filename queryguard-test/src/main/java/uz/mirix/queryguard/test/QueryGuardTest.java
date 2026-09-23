package uz.mirix.queryguard.test;

import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) @Target({ElementType.TYPE,ElementType.METHOD}) @ExtendWith(QueryGuardExtension.class)
public @interface QueryGuardTest {}
