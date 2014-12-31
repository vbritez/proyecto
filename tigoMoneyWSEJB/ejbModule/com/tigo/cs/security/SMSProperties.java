/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tigo.cs.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Miguel Zorrilla
 */
public class SMSProperties {

    private static Properties property = null;

    public static Properties getLog4JConf() throws IOException {
        if (property == null) {
            InputStream in = SMSProperties.class.getResourceAsStream("/resources/log4j.properties");
            property = new java.util.Properties();
            try {
                property.load(in);
            } catch (IOException e) {
                throw e;
            }
        }
        return property;

    }
}
