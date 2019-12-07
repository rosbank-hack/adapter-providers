package ros.hack.providers.service.impl;

import com.github.voteva.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ros.hack.providers.config.KafkaProperties;
import ros.hack.providers.service.ConsumerService;
import ros.hack.providers.service.ProducerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ros.hack.providers.consts.Constants.SERVICE_NAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final KafkaProperties kafkaProperties;
    private final ProducerService producerService;

    @Override
    @Transactional
    @KafkaListener(topics = "${kafka.payment-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@NonNull List<Operation> operations) {
        operations.forEach(operation -> {
            log.info(operation.toString());
            producerService.send(kafkaProperties.getOperationTopic(), addOperation(operation));
        });
    }

    private Operation addOperation(@NonNull Operation operation) {
        com.github.voteva.Service providerService = new com.github.voteva.Service();
        if (operation.getServices() != null
                && operation.getServices().get(SERVICE_NAME) != null) {
            providerService = operation.getServices().get(SERVICE_NAME);
        }

        Map<String, String> request = new HashMap<>();
        if (providerService.getRequest() != null) {
            request = providerService.getRequest();
        }
        Map<String, String> response = request;

        response.put("amount", String.valueOf(Math.random()));

        providerService.setRequest(request);
        providerService.setResponse(response);

        operation.getServices().put(SERVICE_NAME, providerService);
        return operation;
    }
}
