package com.deptagency.sqlexplain;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.deptagency.sqlexplain.listener.DatabaseDialect;
import com.deptagency.sqlexplain.listener.LogExplainPlanQueryListener;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Component
@ConditionalOnProperty(value = "com.deptagency.sqlexplain.enabled", havingValue = "true")
public class ExplainPlanDatasourceProxyBean implements BeanPostProcessor {

    Logger logger = LoggerFactory.getLogger(ExplainPlanDatasourceProxyBean.class);

    // TODO change to a better option to look at the datasource itself instead of
    // relying on the config property
    @Value("${spring.datasource.url:null}")
    private String jdbcURL = null;

    @Value("${com.deptagency.sqlexplain.max_cache_size:50}")
    private Integer maxCacheSize  = null;

    @Value("${com.deptagency.sqlexplain.query_cache_expiry:30}")
    private Integer queryCacheExpiry  = null;

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            Optional<DatabaseDialect> dbDialect = DatabaseDialect.getDatabaseDialectByURL(jdbcURL);

            if (dbDialect.isPresent() && dbDialect.get().isSupported()) {
                ProxyFactory factory = new ProxyFactory(bean);
                factory.setProxyTargetClass(true);
                factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean, dbDialect.get(), maxCacheSize, queryCacheExpiry));
                return factory.getProxy();
            } else {
                logger.warn("WARN database is not currently supported. Currently supported databases include {} ",
                        DatabaseDialect.getSupportedDatabases());
            }
        }
        return bean;
    }

    private static class ProxyDataSourceInterceptor implements MethodInterceptor {
        private final DataSource dataSource;

        public ProxyDataSourceInterceptor(final DataSource dataSource, final DatabaseDialect dialect, final Integer maxCacheSize, final Integer queryCacheExpiry) {
            super();
            this.dataSource = ProxyDataSourceBuilder.create(dataSource)
                    // .countQuery()
                    // .logQueryBySlf4j(SLF4JLogLevel.INFO)
                    .listener(new LogExplainPlanQueryListener(dialect, maxCacheSize, queryCacheExpiry))
                    .build();
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            Method proxyMethod = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
            if (proxyMethod != null) {
                return proxyMethod.invoke(dataSource, invocation.getArguments());
            }
            return invocation.proceed();
        }
    }
}