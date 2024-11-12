package mx.gob.banobras.apitokenizer.infraestructure.adapter.out.client;

/**
 * LdapRestClient.java:
 * 
 * Clase para conectarse la conexion con el servicio de autenticacion en LDAP. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.io.IOException;
import java.util.Date;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import mx.gob.banobras.apitokenizer.application.port.out.ILdapClient;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.common.util.ErrorDetail;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.HttpErrorExceptionDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.LdapResponseDTO;

@Component
public class LdapClientImpl implements ILdapClient {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(LdapClientImpl.class);

	/**
	 * Variable que contiene la URL de conexion con el servicio de autenticacion con
	 * ldap
	 */
	@Value("${app.url.ldap.auth}")
	String urlLdapAuth;

	private final HttpClientFactory httpClientFactory;

	public LdapClientImpl(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
		if(System.getenv("app.url.ldap.auth") != null) {
			urlLdapAuth = System.getenv("app.url.ldap.auth");
		}
	}

	/**
	 * Metodo para validar el usuario que exista en LDAP.
	 * 
	 * @param securityAuthInDTO componente que contiene los datos del token.
	 * 
	 * @return HttpResponse<String> regresa un objeto con los datos del token
	 *         validado
	 * @throws InterruptedException
	 * @throws IOException,         InterruptedException
	 * 
	 */
	@Override
	public LdapResponseDTO authorizationLDAP(TokenizerDTO tokenizerDTO)
			throws Exception {

		Gson gson = new Gson();
		String respondeBody = "";
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		LdapResponseDTO ldapResponseDTO = null;
		ErrorMessageDTO errorMessageDTO = null;
		try {
			log.info("Inicia rest cliente LDAP");
			log.info(urlLdapAuth);

			if (urlLdapAuth.toUpperCase().contains(ConstantsToken.HTTPS.getName())) {
				log.info("Es por HTTPS");
				client = httpClientFactory.getHttpsClient();
			} else {
				client = HttpClients.createDefault();
			}

			HttpPost httpPost = new HttpPost(urlLdapAuth);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("credentials", tokenizerDTO.getCredentials());
			httpPost.setHeader("app-name", tokenizerDTO.getAppName());
			httpPost.setHeader("consumer-id", tokenizerDTO.getConsumerId());
			httpPost.setHeader("functional-id", tokenizerDTO.getFunctionalId());
			httpPost.setHeader("transaction-id", tokenizerDTO.getTransactionalId());
			response = client.execute(httpPost);
			try {

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					respondeBody = EntityUtils.toString(entity);
					ldapResponseDTO = gson.fromJson(respondeBody, LdapResponseDTO.class);
					/** Si la respuesta no tiene datos **/
					if(ldapResponseDTO.getStatusCode() == null) {
						HttpErrorExceptionDTO httpErrorExceptionDTO = gson.fromJson(respondeBody,
								HttpErrorExceptionDTO.class);
						log.info(httpErrorExceptionDTO.getStatus());
						ldapResponseDTO = new LdapResponseDTO();
						ldapResponseDTO.setStatusCode(Integer.parseInt(httpErrorExceptionDTO.getStatus()));
						errorMessageDTO = new ErrorMessageDTO();
						errorMessageDTO.setStatusCode(Integer.parseInt(httpErrorExceptionDTO.getStatus()));
						errorMessageDTO.setTimestamp(new Date());
						errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_LDAP.getName());
						errorMessageDTO.setDetail(httpErrorExceptionDTO.getPath() + 
								" - " + httpErrorExceptionDTO.getError());
						ldapResponseDTO.setErrorMessageDTO(errorMessageDTO);
					}
				}else {
					ldapResponseDTO = new LdapResponseDTO();
					ldapResponseDTO.setStatusCode(503);
					errorMessageDTO = new ErrorMessageDTO();
					errorMessageDTO.setStatusCode(ldapResponseDTO.getStatusCode());
					errorMessageDTO.setTimestamp(new Date());
					errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_LDAP.getName());
					ldapResponseDTO.setErrorMessageDTO(errorMessageDTO);
				}
	
			} finally {
				response.close();
			}
		}
		catch (Exception e) {
			log.error(e);
			ldapResponseDTO = new LdapResponseDTO();
			ldapResponseDTO.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
			errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(ldapResponseDTO.getStatusCode());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_LDAP.getName());
			errorMessageDTO.setDetail(ErrorDetail.getDetail(e));
			ldapResponseDTO.setErrorMessageDTO(errorMessageDTO);
			
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("Finaliza rest cliente LDAP");
		return ldapResponseDTO;
	}

}
