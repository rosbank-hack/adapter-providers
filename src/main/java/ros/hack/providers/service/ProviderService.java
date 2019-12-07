package ros.hack.providers.service;

import ros.hack.providers.model.ProviderDto;

public interface ProviderService {

    ProviderDto getProviderById(String providerId);
}
