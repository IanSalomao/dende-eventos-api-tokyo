package br.com.softhouse.dende.repositories.util;

import br.com.dende.softhouse.annotations.Value;

public class ConfigProperties {

    @Value(key = "datasource.url")
    private String url;

    @Value(key = "datasource.username")
    private String username;

    @Value(key = "datasource.password")
    private String password;

    @Value(key = "datasource.driver-class-name")
    private String driverClassName;

    @Value(key = "datasource.hikari.maximum-pool-size")
    private int maximumPoolSize;

    @Value(key = "datasource.hikari.minimum-idle")
    private int minimumIdle;

    @Value(key = "datasource.hikari.connection-timeout")
    private long connectionTimeout;

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClassName() { return driverClassName; }
    public int getMaximumPoolSize() { return maximumPoolSize; }
    public int getMinimumIdle() { return minimumIdle; }
    public long getConnectionTimeout() { return connectionTimeout; }
}
