package no.fint.cache.annotations;

import no.fint.cache.config.FintCacheConfig;
import no.fint.cache.monitoring.PerformanceConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({FintCacheConfig.class, PerformanceConfig.class})
public @interface EnableFintCache {
}
