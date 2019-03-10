package com.blog4java.spi;

import org.junit.Test;

import java.sql.Driver;
import java.util.ServiceLoader;

public class SPIExample {
    @Test
    public void testSPI() {
        ServiceLoader<Driver> drivers = ServiceLoader.load(java.sql.Driver.class);
        for (Driver driver : drivers ) {
            System.out.println(driver.getClass().getName());
        }
    }
}
