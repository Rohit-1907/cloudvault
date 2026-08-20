package com.cloudvault.util;

import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Slf4j
public class NetworkUtils {

    /**
     * Get the local IP address of the machine
     * Tries to find non-loopback IPv4 address
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                
                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    
                    // Return the first non-loopback IPv4 address
                    if (!address.isLoopbackAddress() && 
                        address.getHostAddress().indexOf(':') == -1) { // IPv4 check
                        String ip = address.getHostAddress();
                        log.info("Detected local IP address: {}", ip);
                        return ip;
                    }
                }
            }
            
            // Fallback to localhost
            log.warn("Could not detect local IP, using localhost");
            return "localhost";
            
        } catch (SocketException e) {
            log.error("Error detecting IP address: {}", e.getMessage());
            return "localhost";
        }
    }
    
    /**
     * Build base URL with auto-detected IP or configured value
     */
    public static String buildBaseUrl(String configuredUrl, int serverPort) {
        // If configured URL is set and not placeholder, use it
        if (configuredUrl != null && 
            !configuredUrl.isEmpty() && 
            !configuredUrl.contains("localhost")) {
            return configuredUrl;
        }
        
        // Auto-detect IP
        String ip = getLocalIpAddress();
        String baseUrl = "http://" + ip + ":" + serverPort;
        log.info("Using base URL: {}", baseUrl);
        return baseUrl;
    }
}
