package ros.hack.providers.service;

import com.github.voteva.Operation;

public interface ProducerService {
    void send(String topic, Operation operation);
}