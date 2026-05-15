package br.com.softhouse.dende.repositories.util;

import br.com.dende.softhouse.annotations.Value;
import java.io.InputStream;
import java.util.Properties;

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

    public ConfigProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);

                this.url = prop.getProperty("datasource.url");
                this.username = prop.getProperty("datasource.username");
                this.password = prop.getProperty("datasource.password");
                this.driverClassName = prop.getProperty("datasource.driver-class-name");
                this.maximumPoolSize = Integer.parseInt(prop.getProperty("datasource.hikari.maximum-pool-size", "10"));
                this.minimumIdle = Integer.parseInt(prop.getProperty("datasource.hikari.minimum-idle", "2"));
                this.connectionTimeout = Long.parseLong(prop.getProperty("datasource.hikari.connection-timeout", "30000"));
            } else {
                System.err.println("Aviso: O ficheiro application.properties não foi encontrado na pasta resources!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar propriedades: " + e.getMessage());
        }
    }

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClassName() { return driverClassName; }
    public int getMaximumPoolSize() { return maximumPoolSize; }
    public int getMinimumIdle() { return minimumIdle; }
    public long getConnectionTimeout() { return connectionTimeout; }
}