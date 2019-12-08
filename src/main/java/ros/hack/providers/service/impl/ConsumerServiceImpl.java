package ros.hack.providers.service.impl;

import com.github.voteva.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ros.hack.providers.config.KafkaProperties;
import ros.hack.providers.model.ProviderDto;
import ros.hack.providers.service.ConsumerService;
import ros.hack.providers.service.ProducerService;
import ros.hack.providers.service.ProviderService;

import java.util.HashMap;
import java.util.Map;

import static ros.hack.providers.consts.Constants.*;
import static ros.hack.providers.utils.JsonParser.parse;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService<String, String> {

    private final KafkaProperties kafkaProperties;
    private final ProducerService producerService;
    private final ProviderService providerService;

    @Override
    @Transactional
    @KafkaListener(topics = "${kafka.payment-topic}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${kafka.group-id")
    public void consume(@NonNull ConsumerRecord<String, String> consumerRecord) {
        log.info(consumerRecord.toString());
        producerService.send(kafkaProperties.getOperationTopic(), (addOperation(parse(consumerRecord.value()))));
    }

    private Operation addOperation(@NonNull Operation operation) {
        com.github.voteva.Service provider = new com.github.voteva.Service();
        if (operation.getServices() != null
                && operation.getServices().get(SERVICE_NAME) != null) {
            provider = operation.getServices().get(SERVICE_NAME);
        }

        Map<String, String> request = new HashMap<>();
        if (provider.getRequest() != null) {
            request = provider.getRequest();
        }
        Map<String, String> response = request;

        ProviderDto providerDto = providerService.getProviderById(request.get(PROVIDER_ID));

        if (providerDto != null) {
            response.put(PROVIDER_ID, providerDto.getProviderId());
            response.put(PROVIDER_NAME, providerDto.getProviderName());
            response.put(PROVIDER_MCC, providerDto.getProviderMcc());
            response.put(PROVIDER_ICON, providerDto.getProviderIcon());
            operation.setMcc(providerDto.getProviderMcc());
        }

        provider.setRequest(request);
        provider.setResponse(response);

        operation.getServices().put(SERVICE_NAME, provider);
        return operation;
    }
}
