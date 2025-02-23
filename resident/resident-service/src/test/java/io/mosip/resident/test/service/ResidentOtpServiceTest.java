package io.mosip.resident.test.service;

import io.mosip.resident.constant.ApiName;
import io.mosip.resident.dto.OtpRequestDTO;
import io.mosip.resident.dto.OtpResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.ResidentOtpService;
import io.mosip.resident.service.impl.ResidentOtpServiceImpl;
import io.mosip.resident.util.AuditUtil;
import io.mosip.resident.util.ResidentServiceRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class ResidentOtpServiceTest {

    @Mock
    private ResidentServiceRestClient residentServiceRestClient;

    @Mock
    Environment env;

    @Mock
    private AuditUtil audit;

    @InjectMocks
    private ResidentOtpService residentOtpService = new ResidentOtpServiceImpl();

    @Before
    public void setup() {
    }

    @Test
    public void testGenerateOtp() throws ApisResourceAccessException {
        String otpAPIUrl = "https://dev2.mosip.net/idauthentication/v1/internal/otp";
        OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
        when(env.getProperty(ApiName.OTP_GEN_URL.name())).thenReturn(otpAPIUrl);
        when(residentServiceRestClient.postApi(anyString(), any(), any(), any(Class.class))).thenReturn(otpResponseDTO);

        OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
        residentOtpService.generateOtp(otpRequestDTO);

        verify(residentServiceRestClient, times(1)).postApi(anyString(), any(), any(), any(Class.class));
        verify(env, times(1)).getProperty(ApiName.OTP_GEN_URL.name());
    }

    @Test(expected = ResidentServiceException.class)
    public void testGenerateOtpThrowsResidentServiceException() throws ApisResourceAccessException {
        String otpAPIUrl = "https://dev2.mosip.net/idauthentication/v1/internal/otp";
        when(env.getProperty(ApiName.OTP_GEN_URL.name())).thenReturn(otpAPIUrl);
        when(residentServiceRestClient.postApi(anyString(), any(), any(), any(Class.class))).thenThrow(new ApisResourceAccessException());

        OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
        residentOtpService.generateOtp(otpRequestDTO);
    }

}