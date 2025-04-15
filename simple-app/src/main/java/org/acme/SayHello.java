package org.acme;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SayHello {
    
    public String sayHello(String name ){

        System.out.println("Hello " + name);
        return "Hello "+ name;
    }
}
