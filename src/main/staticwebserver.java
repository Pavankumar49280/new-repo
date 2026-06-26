package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticWebsiteServer {
    
    private static final int DEFAULT_PORT = 8080;
    private static final String STATIC_RESOURCE_PATH = "/static";
    
    public static void main(String[] args) throws Exception {
        // Create server
        Server server = new Server();
        
        // Create connector with port
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(getPort(args));
        server.addConnector(connector);
        
        // Create servlet context handler
        ServletContextHandler context = new ServletContextHandler(
            ServletContextHandler.SESSIONS
        );
        context.setContextPath("/");
        context.setBaseResource(getStaticResource());
        
        // Add default servlet to serve static files
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("dirAllowed", "true");
        defaultServlet.setInitParameter("welcomeServlets", "true");
        defaultServlet.setInitParameter("redirectWelcome", "true");
        context.addServlet(defaultServlet, "/");
        
        server.setHandler(context);
        
        // Start server
        server.start();
        
        // Print server information
        System.out.println("============================================");
        System.out.println("Static Website Server Started!");
        System.out.println("Server running at: http://localhost:" + connector.getPort());
        System.out.println("Serving static files from: " + getStaticResourcePath());
        System.out.println("Press CTRL+C to stop");
        System.out.println("============================================");
        
        // Wait for server to stop
        server.join();
    }
    
    private static Resource getStaticResource() {
        try {
            // Try to find static resources in classpath
            URL resourceUrl = StaticWebsiteServer.class
                .getResource(STATIC_RESOURCE_PATH);
            
            if (resourceUrl != null) {
                return Resource.newResource(resourceUrl);
            }
            
            // Fallback to file system
            Path staticPath = Paths.get("src/main/resources/static").toAbsolutePath();
            if (staticPath.toFile().exists()) {
                return Resource.newResource(staticPath.toFile());
            }
            
            // If not found, create default static directory
            System.err.println("Warning: Static resources not found. Creating default directory.");
            staticPath.toFile().mkdirs();
            return Resource.newResource(staticPath.toFile());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate static resources", e);
        }
    }
    
    private static String getStaticResourcePath() {
        try {
            URL resourceUrl = StaticWebsiteServer.class
                .getResource(STATIC_RESOURCE_PATH);
            if (resourceUrl != null) {
                return resourceUrl.toString();
            }
            return Paths.get("src/main/resources/static").toAbsolutePath().toString();
        } catch (Exception e) {
            return "Unknown location";
        }
    }
    
    private static int getPort(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }
}
