package com.guudint.clickargo.clictruck.scheduler.sg.shell;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.InvoicingDiscount;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.PriceTransactionRequestDTO;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.ShellCredentialConfigDto;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellBatchWindow;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCardTruck;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellBatchWindowServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellCardServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellCardTruckServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellInvoiceItemServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellInvoiceServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellKioskServiceImpl;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellTxnServiceImpl;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.guudint.clickargo.common.service.ICkSeqNoService;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;

@Component
@EnableAsync
public abstract class AbstractShellScheduler extends AbstractClickTruckScheduler {

    private static final Logger log = Logger.getLogger(AbstractShellScheduler.class);
    protected static ObjectMapper mapper = new ObjectMapper();

    protected static final String TOKEN_ENDPOINT = "/oauth/v1/mobility/token";
    protected static final char STATUS_ACTIVE = 'A';
    protected static final char STATUS_EXPIRED = 'E';
    protected static final char STATUS_INACTIVE = 'I';

    protected static final BigDecimal GST_TAX = BigDecimal.valueOf(9.0);
    protected static final BigDecimal CO2_COMPENSATION_FEE  = BigDecimal.valueOf(0.06);

    @Autowired
    @Qualifier("coreSysparamDao")
    protected GenericDao<TCoreSysparam, String> coreSysparamDao;

    @Autowired
    @Qualifier("coreAccDao")
    private GenericDao<TCoreAccn, String> coreAccDao;

    @Autowired
    protected ICkSeqNoService seqnoService;

    @Autowired
    protected CkCtShellCardServiceImpl ckCtShellCardService;

    @Autowired
    protected CkCtShellKioskServiceImpl ckCtShellKioskService;

    @Autowired
    protected CkCtShellTxnServiceImpl ckCtShellTxnService;

    @Autowired
    protected CkCtShellInvoiceServiceImpl ckCtShellInvoiceService;

    @Autowired
    protected CkCtShellCardTruckServiceImpl ckCtShellCardTruckService;

    @Autowired
    protected CkCtShellInvoiceItemServiceImpl ckCtShellInvoiceItemService;

    @Autowired
    protected CkCtShellBatchWindowServiceImpl ckCtShellBatchWindowService;

    public ResponseEntity<String> getMobilityToken(String constant) {
        try {
            ShellCredentialConfigDto config = getShellCredentialConfig(constant);
            String authStr = config.getApikey() + ":" + config.getSecret();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + Base64Utils.encodeToString(authStr.getBytes()));

            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(param, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(config.getUrl() + TOKEN_ENDPOINT, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Error getting mobility token", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> makeApiRequest(String endpoint, String constant, String module, boolean useToken) {
        try {
            ShellCredentialConfigDto config = getShellCredentialConfig(constant);
            HttpHeaders headers;
            String requestBody = null;
            HttpMethod method;
            String url;

            if (useToken) {
                ResponseEntity<String> tokenResponse = getMobilityToken(CtConstant.KEY_CLICTRUCK_SHEL_CREDENTIAL_TYPE);
                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                    log.error("Failed to retrieve access token in makeApiRequest");
                    return new ResponseEntity<>(tokenResponse.getStatusCode());
                }

                String token = new JSONObject(tokenResponse.getBody()).getString("access_token");
                headers = createBearerAuthHeaders(token);
                method = HttpMethod.GET;
                url = createUrlTemplate(config.getUrl() + endpoint);
            } else {
                headers = createBasicAuthHeaders(config, module);
                requestBody = createRequestBody(config, module);
                method = HttpMethod.POST;
                url = config.getSecondaryUrl() + endpoint;
            }

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(url, method, entity, String.class);
        } catch (Exception e) {
            log.error("Error in makeApiRequest", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders createBasicAuthHeaders(ShellCredentialConfigDto config, String module) {
        HttpHeaders headers = new HttpHeaders();
        String authStr;
        if ("PRICE_TRANSACTION".equals(module)) {
            headers.set("apikey", config.getApikeyT1());
            authStr = config.getApikeyT1() + ":" + config.getSecretT1();
        } else {
            headers.set("apikey", config.getApikey());
            authStr = config.getApikey() + ":" + config.getSecret();
        }
        String encodedAuth = Base64Utils.encodeToString(authStr.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    private HttpHeaders createBearerAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("RequestId", UUID.randomUUID().toString());
        return headers;
    }

    private String createRequestBody(ShellCredentialConfigDto config, String module) throws Exception {
        if ("PRICE_TRANSACTION".equals(module)) {
            TCkCtShellBatchWindow batch = ckCtShellBatchWindowService.find(CtConstant.SHELL_CARD_TXN);
            return createPriceTransactionRequestBody(config, batch.getSbDtWindow());
        } else {
            return createShellCardRequestBody(config.getPayerNumber2());
        }
    }

    private String createPriceTransactionRequestBody(ShellCredentialConfigDto config, Date batchDate) throws JsonProcessingException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String fromDate = formatter.format(batchDate);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minus(1, ChronoUnit.DAYS);
        DateTimeFormatter toDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String toYesterday = yesterday.format(toDateFormat);

        PriceTransactionRequestDTO requestDTO = new PriceTransactionRequestDTO(fromDate, toYesterday, config);

        // Convert DTO to JSON string
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO);
    }

    private ShellCredentialConfigDto getShellCredentialConfig(String constant) throws Exception {
        String credential = getSysParam(constant);
        return mapper.readValue(credential, ShellCredentialConfigDto.class);
    }

    private String createUrlTemplate(String baseUrl) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("countryCode", "SG")
                .queryParam("pageSize", 250)
                .queryParam("page", 1)
                .toUriString();
    }

    protected String getSysParam(String key) throws Exception {
        if (StringUtils.isBlank(key))
            throw new ParameterException("param key null or empty");
        TCoreSysparam sysParam = coreSysparamDao.find(key);
        if (sysParam != null) {
            return sysParam.getSysVal();
        }
        throw new EntityNotFoundException("sys param config " + key + " not set");
    }

    private String createShellCardRequestBody(String payerNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PayerNumber", payerNumber);
        JSONArray cardStatusArray = new JSONArray();
        cardStatusArray.put("ALL");
        jsonObject.put("CardStatus", cardStatusArray);
        return jsonObject.toString(2);
    }

    protected boolean checkExistingCard(String id) throws Exception {
        if (StringUtils.isBlank(id))
            throw new ParameterException("param key null or empty");

        try {
            TCkCtShellCard shellCard = ckCtShellCardService.find(id);
            return shellCard != null;
        } catch (Exception e) {
            log.error("checkExistingCard", e);
        }
        return false;
    }

    protected boolean checkExistingKiosk(String id) throws Exception {
        if (StringUtils.isBlank(id))
            throw new ParameterException("param key null or empty");

        try {
            TCkCtShellKiosk value = ckCtShellKioskService.find(id);
            return value != null;
        } catch (Exception e) {
            log.error("checkExistingCard", e);
        }
        return false;
    }

    protected static boolean isCardExpired(String expiryDateStr) throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, formatter);
            LocalDate today = LocalDate.now();
            return expiryDate.isBefore(today);
        } catch (Exception e) {
            log.error("isCardExpired", e);
        }
        return false;
    }

    protected boolean isExistingAccn(String id) throws Exception {
        if (StringUtils.isBlank(id))
            throw new ParameterException("param key null or empty");

        try {
            TCoreAccn value = coreAccDao.find(id);
            return value != null;
        } catch (Exception e) {
            log.error("isExistingAccn", e);
        }
        return false;
    }

    protected List<InvoicingDiscount> discountList() throws Exception {

        List<InvoicingDiscount> discountListConfig = null;
        try {
            String value = this.getSysParam(CtConstant.KEY_CLICTRUCK_SHELL_INVOICING_DISCOUNT);
            discountListConfig = mapper.readValue(value, mapper.getTypeFactory().constructCollectionType(List.class, InvoicingDiscount.class));
        } catch (Exception e) {
            log.error("discountList", e);
        }
        return discountListConfig;
    }

    public TCkCtShellCard findCardById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.info("findCardById");
        TCkCtShellCard tCkCtShellCard =  null;
        try {
            tCkCtShellCard = ckCtShellCardService.find(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            log.error("findCardById", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("findCardById", ex);
            throw new ProcessingException(ex);
        }
        return tCkCtShellCard;
    }

    public TCkCtShellCardTruck findAssignedTruckCardById(String id, String accnId) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.info("findAssignedTruckCardById");
        TCkCtShellCardTruck assignedTruck =  null;
        try {
            assignedTruck = ckCtShellCardTruckService.findByCard(id, accnId);
        } catch (ParameterException | EntityNotFoundException ex) {
            log.error("findAssignedTruckCardById", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("findAssignedTruckCardById", ex);
            throw new ProcessingException(ex);
        }
        return assignedTruck;
    }

    public TCkCtShellCardTruck findAssignedTruckCardById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.info("findAssignedTruckCardById");
        TCkCtShellCardTruck assignedTruck =  null;
        try {
            assignedTruck = ckCtShellCardTruckService.findByCard(id);
        } catch (ParameterException | EntityNotFoundException ex) {
            log.error("findAssignedTruckCardById", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("findAssignedTruckCardById", ex);
            throw new ProcessingException(ex);
        }
        return assignedTruck;
    }
}
