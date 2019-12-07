package ros.hack.providers.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProviderDto {
    private String providerId;
    private String providerName;
    private String providerIcon;
    private String providerMcc;
}
