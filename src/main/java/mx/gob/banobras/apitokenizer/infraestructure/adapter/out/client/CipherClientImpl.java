package mx.gob.banobras.apitokenizer.infraestructure.adapter.out.client;
/**
 * CipherClientImpl.java:
 * 
 * Clase para conectarse con el servicio de cifrado de datos. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */


import java.util.Date;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;

import mx.gob.banobras.apitokenizer.application.port.out.ICipherClient;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.common.util.ErrorDetail;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.CipherResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.DataDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.HttpErrorExceptionDTO;

@Component
public class CipherClientImpl implements ICipherClient {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(CipherClientImpl.class);

	/**
	 * Variable que contiene la URL de conexion con el servicio de autenticacion con
	 * ldap
	 */
	@Value("${app.url.cipher.decode}")
	String urlCipherDecode;

	private final HttpClientFactory httpClientFactory;

	public CipherClientImpl(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;

	}

	/**
	 * Metodo para descriptar los datos de entrada.
	 * 
	 * @param dataDTO contiene los datos encrptados.
	 * @return regresa el objeto CipherResponseDTO con los datos desencriptados.
	 * 
	 * @throws Exception excepción durante el proceso desencriptar.
	 */
	@Override
	public CipherResponseDTO decode(TokenizerDTO tokenizerDTO) throws Exception {
		Gson gson = new Gson();
		String respondeBody = "";
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		CipherResponseDTO cipherResponseDTO = null;
		HttpPost httpPost = null;

		DataDTO dataDTO = new DataDTO();
		dataDTO.setData(tokenizerDTO.getCredentials());

		try {
			log.info("Inicia rest cliente cipher enconde");
			log.info(urlCipherDecode);

			if (urlCipherDecode.toUpperCase().contains(ConstantsToken.HTTPS.getName())) {
				log.info("Es por HTTPS");
				client = httpClientFactory.getHttpsClient();
			} else {
				client = HttpClients.createDefault();
			}
			httpPost = new HttpPost(urlCipherDecode);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("data", dataDTO.getData());
			response = client.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					log.info("Conexion y respuesta exitosa.");
					respondeBody = EntityUtils.toString(entity);
					cipherResponseDTO = gson.fromJson(respondeBody, CipherResponseDTO.class);
					/** Si los datos se desencriptaron correctamente **/
					if (cipherResponseDTO.getStatusCode() != null) {
						log.info(cipherResponseDTO.getStatusCode());
						if (cipherResponseDTO.getStatusCode() == 200) {
							String[] valDecrypt = cipherResponseDTO.getDataDTO().getData().split(" ");
							if (valDecrypt.length >= 2) {
								cipherResponseDTO.getDataDTO().setUserName(valDecrypt[0]);
								cipherResponseDTO.getDataDTO().setPassword(valDecrypt[1]);
							} else if (valDecrypt.length == 1) {
								cipherResponseDTO.getDataDTO().setUserName(valDecrypt[0]);
								cipherResponseDTO.getDataDTO().setPassword("");
							}
						}
					} else {
						HttpErrorExceptionDTO httpErrorExceptionDTO = gson.fromJson(respondeBody,
								HttpErrorExceptionDTO.class);
						log.info(httpErrorExceptionDTO.getStatus());
						cipherResponseDTO = new CipherResponseDTO();
						cipherResponseDTO.setStatusCode(Integer.parseInt(httpErrorExceptionDTO.getStatus()));
						ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
						errorMessageDTO.setStatusCode(Integer.parseInt(httpErrorExceptionDTO.getStatus()));
						errorMessageDTO.setTimestamp(new Date());
						errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_DECODE.getName());
						errorMessageDTO.setDetail(httpErrorExceptionDTO.getPath() + 
								" - " + httpErrorExceptionDTO.getError());
						cipherResponseDTO.setErrorMessageDTO(errorMessageDTO);
					}
				} else {
					log.info("Respuesta nula.");
					cipherResponseDTO = new CipherResponseDTO();
					cipherResponseDTO.setStatusCode(503);
					ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
					errorMessageDTO.setStatusCode(cipherResponseDTO.getStatusCode());
					errorMessageDTO.setTimestamp(new Date());
					errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_DECODE.getName());
					cipherResponseDTO.setErrorMessageDTO(errorMessageDTO);
				}
			} finally {
				response.close();
			}

		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			log.error(e);
			cipherResponseDTO = new CipherResponseDTO();
			cipherResponseDTO.setStatusCode(503);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(cipherResponseDTO.getStatusCode());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_NO_SERVICE_DECODE.getName());
			errorMessageDTO.setDetail(ErrorDetail.getDetail(e));
			cipherResponseDTO.setErrorMessageDTO(errorMessageDTO);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			cipherResponseDTO = new CipherResponseDTO();
			cipherResponseDTO.setStatusCode(500);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(cipherResponseDTO.getStatusCode());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_ERROR_500.getName());
			errorMessageDTO.setDetail(ErrorDetail.getDetail(e));
			cipherResponseDTO.setErrorMessageDTO(errorMessageDTO);
		} finally {
			client.close();
		}
		log.info("Finaliza rest cliente Decode");
		return cipherResponseDTO;
	}

}
