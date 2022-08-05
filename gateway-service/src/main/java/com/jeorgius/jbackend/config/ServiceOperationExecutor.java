package com.jeorgius.jbackend.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ServiceOperationExecutor {

    private final ConsulClient consulClient;

    public ServiceOperationExecutor(
            @Value("${spring.cloud.consul.host}") String consulHost,
            @Value("${spring.cloud.consul.port}") int consulPort) {

        consulClient = new ConsulClient(consulHost, consulPort);
    }

    public <R> R executeForService(String serviceName, ServiceInstanceOperation serviceInstanceOperation) {
        var serviceUrlOpt = getServiceUrl(serviceName);
        if (serviceUrlOpt.isEmpty()) {
            throw new IllegalStateException(String.format("Unable to determine service url for service name [%s]", serviceName));
        }

        return serviceInstanceOperation.execute(serviceUrlOpt.get());
    }

    private Optional<String> getServiceUrl(String serviceName) {
        var response = consulClient.getHealthServices(serviceName, true, QueryParams.DEFAULT);
        var services = response.getValue();
        if (services != null && !services.isEmpty()) {
            return Optional.of(String.format("http://%s:%d", services.get(0).getService().getAddress(), services.get(0).getService().getPort()));
        }

        return Optional.empty();
    }
}
