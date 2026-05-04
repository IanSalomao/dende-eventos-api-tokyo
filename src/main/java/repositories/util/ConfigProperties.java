package repositories.util;

import br.com.dende.softhouse.annotation.Value;

public class ConfigProperties {

    @Value("datasource.url")
    private String url;

    @Value("datasource.username")
    private String username;

    @Value("datasource.password")
    private String password;

    @Value("datasource.driver-class-name")
    private String driverClassName;

    @Value("datasource.hikari.maximum-pool-size")
    private int maximumPoolSize;

    @Value("datasource.hikari.minimum-idle")
    private int minimumIdle;

    @Value("datasource.hikari.connection-timeout")
    private long connectionTimeout;

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClassName() { return driverClassName; }
    public int getMaximumPoolSize() { return maximumPoolSize; }
    public int getMinimumIdle() { return minimumIdle; }
    public long getConnectionTimeout() { return connectionTimeout; }
}
