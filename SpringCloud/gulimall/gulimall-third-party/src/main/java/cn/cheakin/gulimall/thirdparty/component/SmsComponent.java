    package cn.cheakin.gulimall.thirdparty.component;

    import com.aliyun.auth.credentials.Credential;
    import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
    import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
    import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
    import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
    import com.google.gson.Gson;
    import darabonba.core.client.ClientOverrideConfiguration;
    import lombok.Data;
    import lombok.SneakyThrows;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;

    import java.util.concurrent.CompletableFuture;
    
    @ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
    @Data
    @Component
    public class SmsComponent {
    
        private String accessKey;
        private String secretKey;
        private String region;
        private String endpoint;
        private String signName;
        private String templateCode;
    

        @SneakyThrows
        public void sendCode(String phone, String code) {
            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                    .accessKeyId(accessKey)
                    .accessKeySecret(secretKey)
                    //.securityToken("<your-token>") // use STS token
                    .build());
    
            // Configure the Client
            AsyncClient client = AsyncClient.builder()
                    .region(region) // Region ID
                    //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                    .credentialsProvider(provider)
                    //.serviceConfiguration(Configuration.create()) // Service-level configuration
                    // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                    .overrideConfiguration(
                            ClientOverrideConfiguration.create()
                                    .setEndpointOverride(endpoint)
                            //.setConnectTimeout(Duration.ofSeconds(30))
                    )
                    .build();
    
            // Parameter settings for API request
            SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                    .signName(signName)
                    .templateCode(templateCode)
                    .phoneNumbers(phone)
                    .templateParam("{\"code\": " + code + "}")
                    // Request-level configuration rewrite, can set Http request parameters, etc.
                    // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                    .build();
    
            // Asynchronously get the return value of the API request
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            // Synchronously get the return value of the API request
            SendSmsResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
            // Asynchronous processing of return values
            /*response.thenAccept(resp -> {
                System.out.println(new Gson().toJson(resp));
            }).exceptionally(throwable -> { // Handling exceptions
                System.out.println(throwable.getMessage());
                return null;
            });*/
    
            // Finally, close the client
            client.close();
        }
    
    }