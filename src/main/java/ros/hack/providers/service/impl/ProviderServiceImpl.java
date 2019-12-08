package ros.hack.providers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ros.hack.providers.model.ProviderDto;
import ros.hack.providers.service.ProviderService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ObjectMapper objectMapper;

    @Override
    public ProviderDto getProviderById(String providerId) {
        ProviderDto providerDto = new ProviderDto();
        List<ProviderDto> providers;
        try {
            File file = new File(
                    getClass().getClassLoader().getResource("providers.json").getFile());
            providers = Arrays.asList(objectMapper.readValue(file, ProviderDto[].class));
            providerDto = providers.stream()
                    .filter(provider -> providerId.equals(provider.getProviderId()))
                    .findAny()
                    .orElse(null);
        } catch (IOException e) {
            log.error("IOException: " + e);
        } catch (NullPointerException e) {
            log.error("File was not found");
        }
        return providerDto;
    }
}
