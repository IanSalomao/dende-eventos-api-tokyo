package br.com.softhouse.dende;

import br.com.dende.softhouse.process.WebApplication;

import java.io.IOException;

public class DendeEventosApplication {
    public static void main(String[] args) throws IOException {

        WebApplication webApplication = new WebApplication(DendeEventosApplication.class);
        webApplication
                .getContext()
                .getAllBeans()
                .forEach(object -> {
                    System.out.println(object.getClass().getName());
                });
        webApplication.run();

    }
}