package mx.gob.banobras.apitokenizer.infraestructure.adapter.in.controller;

/**
 * TokenizerPublicController.java:
 * 
 * Clase controller que expone los servicios Rest para validar y generar el
 * token para los sitema publicos.
 * 
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import mx.gob.banobras.apitokenizer.application.port.in.ITokenizerCasoUsoService;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.common.util.ErrorDetail;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.TokenizerResponseDTO;

@CrossOrigin(originPatterns = { "*" })
@RestController
public class TokenizerPublicController implements ITokenizerPublicController {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(TokenizerPublicController.class);

	/** Injection variable del objeto HttpServletRequest */
	private HttpServletRequest httRequest;

	/** Injection variable para la interfaz iTokenizerInputPort */
	private ITokenizerCasoUsoService iTokenizerInputPort;

	/** Consturctor de las interfaces que usa el controller */
	public TokenizerPublicController(ITokenizerCasoUsoService iTokenizerInputPort, HttpServletRequest httRequest) {
		this.iTokenizerInputPort = iTokenizerInputPort;
		this.httRequest = httRequest;
	}

	/**
	 * Metodo para obtener el Token, para el consumo de los microservicios.
	 * 
	 * @param userName        - Alias del usuario.
	 * @param consumer-api-id - Nombre de la intefaz que lo va a consumir el
	 *                        microservicio.
	 * @param functional-id   - Acronimo de la funcionalidad.
	 * @param transaction-id  - Identificador de la transacción generado por UUDI.
	 * @param refresh-token   - Tiempo de duracion del refresh Token, si el valor es
	 *                        Cero, no se genera.
	 * 
	 * @return regresa el obejto TokenizerResponseDTO con los datos del Token
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 * 
	 */
	public ResponseEntity<TokenizerResponseDTO> getTokenPublic(@RequestHeader(value = "credentials") String credentials,
			@RequestHeader(value = "app-name") String appName, @RequestHeader(value = "consumer-id") String consumerId,
			@RequestHeader(value = "functional-id") String functionalId,
			@RequestHeader(value = "transaction-id") String transactionId) {

		TokenizerResponseDTO tokenizerResponseDTO = null;
		TokenizerDTO tokenizerDTO = null;
		ErrorMessageDTO errorMessage = null;
		try {
			/** String remoteHost = request.getRemoteHost(); **/
			String remoteHost = getIpRemoteAdress();

			/** Agrega parametros para que se muestren en el Log **/
			ThreadContext.put("transaction-id", transactionId);
			ThreadContext.put("ip", remoteHost);

			log.info("Inicia generar token - publico");

			/** Verifica que las credenciales no esten vacias **/
			if (!credentials.isEmpty()) {
				/** Inicializa el obejto con los valores de header */
				tokenizerDTO = new TokenizerDTO(credentials, null, null, null, appName, consumerId, functionalId,
						transactionId, 0);
				log.info("Invoca la intefaz para obtiener el token");
				tokenizerResponseDTO = iTokenizerInputPort.createTokenPublic(tokenizerDTO);
			} else {
				log.info("Credenciales vacias");
				errorMessage = new ErrorMessageDTO();
				errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorMessage.setTimestamp(new Date());
				errorMessage.setMessage(ConstantsToken.MSG_CREDENTIALS_EMPTY.getName());
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), e);
			errorMessage = new ErrorMessageDTO();
			errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessage.setTimestamp(new Date());
			errorMessage.setMessage(e.getMessage());
			errorMessage.setDetail(ErrorDetail.getDetail(e));
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
		} finally {
			ThreadContext.clearStack();
			log.info("Finaliza generar token - publico");
		}
		return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.valueOf(tokenizerResponseDTO.getStatusCode()));
	}

	/**
	 * Metodo para obtener el Token, para el consumo de los microservicios.
	 * 
	 * @param userName        - Alias del usuario.
	 * @param auth-token      - Token generado por JWT, para su validacion.
	 * @param x-api-id        - Id de servicio que se va a consumir.
	 * @param consumer-api-id - Nombre de la intefaz que lo va a consumir el
	 *                        microservicio.
	 * @param functional-id   - Acronimo de la funcionalidad.
	 * @param transaction-id  - Identificador de la transacción generado por UUDI.
	 * 
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */

	public ResponseEntity<TokenizerResponseDTO> validTokenPublic(
			@RequestHeader(value = "credentials") String credentials,
			@RequestHeader(value = "token-auth") String tokenAuth, @RequestHeader(value = "app-name") String appName,
			@RequestHeader(value = "consumer-id") String consumerId,
			@RequestHeader(value = "functional-id") String functionalId,
			@RequestHeader(value = "transaction-id") String transactionId) {

		TokenizerResponseDTO tokenizerResponseDTO = null;
		TokenizerDTO tokenizerDTO = null;
		ErrorMessageDTO errorMessage = null;
		try {
			String remoteHost = getIpRemoteAdress();
			ThreadContext.put("transaction-id", transactionId);
			ThreadContext.put("ip", remoteHost);

			log.info("Inicia validar token - publico");

			/** Verifica que las credenciales no esten vacias **/
			if (!credentials.isEmpty()) {
				tokenizerDTO = new TokenizerDTO(credentials, null, null, tokenAuth, appName, consumerId, functionalId,
						transactionId, null);

				log.info("Invoca la intefaz para validar el token");
				tokenizerResponseDTO = iTokenizerInputPort.validateTokenPublic(tokenizerDTO);

			} else {
				log.info("Credenciales vacias");
				errorMessage = new ErrorMessageDTO();
				errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorMessage.setTimestamp(new Date());
				errorMessage.setMessage(ConstantsToken.MSG_CREDENTIALS_EMPTY.getName());
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessage);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), e);
			errorMessage = new ErrorMessageDTO();
			errorMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessage.setTimestamp(new Date());
			errorMessage.setMessage(e.getMessage());
			errorMessage.setDetail(ErrorDetail.getDetail(e));
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessage);

		} finally {
			ThreadContext.clearStack();
			log.info("Finaliza validar token - publico");
		}
		return new ResponseEntity<>(tokenizerResponseDTO, HttpStatus.valueOf(tokenizerResponseDTO.getStatusCode()));
	}

	/**
	 * Metodo para obtener la IP del srevicio que invoca al controller.
	 * 
	 * @throws UnknownHostException
	 * 
	 * @throws Exception            Excepción durante el proceso de generar el
	 *                              Token.
	 */
	private String getIpRemoteAdress() throws UnknownHostException {
		log.info(new StringBuilder().append("Busca IP :: ").append(httRequest.getRemoteHost()));
		String ipAddress = obtenValorPorRemoteAddr(httRequest.getRemoteHost());

		if (ipAddress == null || ipAddress.length() == 0) {
			if (buscaEnHeaderPor(ConstantsToken.H_X_FORWARDED_FOR.getName())) {
				ipAddress = httRequest.getHeader(ConstantsToken.H_X_FORWARDED_FOR.getName());
			} else {
				if (buscaEnHeaderPor(ConstantsToken.H_PROXY_CLIENT_IP.name())) {
					ipAddress = httRequest.getHeader(ConstantsToken.H_PROXY_CLIENT_IP.getName());
				} else {
					if (buscaEnHeaderPor(ConstantsToken.H_WL_PROXY_CLIENT_IP.getName())) {
						ipAddress = httRequest.getHeader(ConstantsToken.H_WL_PROXY_CLIENT_IP.getName());
					} else {
						ipAddress = "0.0.0.X";
					}
				}
			}
		}
		log.info(new StringBuilder().append("La ip remota es :: ").append(ipAddress));
		return ipAddress;
	}

	/**
	 * Metodo para obtener la IP del srevicio que invoca al controller en el Header
	 * de la petición
	 * 
	 * @param cadenaABuscar cadena que contien el filtro a buscar en el header
	 * @return regresa un valor boolean, true si lo encuentra el valor
	 * 
	 */
	private boolean buscaEnHeaderPor(String cadenaABuscar) {
		log.info(new StringBuilder().append(ConstantsToken.BUSCA_POR.getName()).append(cadenaABuscar));
		String ipAddress = httRequest.getHeader(cadenaABuscar);
		boolean bandera = true;
		if (ipAddress == null || ipAddress.isEmpty() || ConstantsToken.UNKNOWN.getName().equalsIgnoreCase(ipAddress)) {
			log.info("Ip no encontrada");
			bandera = false;
		}
		return bandera;
	}

	/**
	 * Metodo para obtener la IP del srevicio que invoca al controller con el metodo
	 * geRemoteAdrr() de la petición
	 * 
	 * @return regresa un valor de la IP en formato IPV4 o IPV6
	 * 
	 * @throws UnknownHostException Exception en caso de no poder validar los datos.
	 */
	private String obtenValorPorRemoteAddr(String ipAddress) throws UnknownHostException {
		log.info("obtenValorPorRemoteAddr()");
		if (ipAddress.equals(ConstantsToken.LOCALHOST_IPV4.getName())
				|| ipAddress.equals(ConstantsToken.LOCALHOST_IPV6.getName())) {
			log.info("IPV4 - IPV5");
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
			log.info(ipAddress);
		}
		if (ipAddress != null && ipAddress.length() > 15 && ipAddress.contains(ConstantsToken.COMMA.getName())) {
			log.info("length > 15 - contains comma");
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(ConstantsToken.COMMA.getName()));
			log.info(ipAddress);
		}
		return ipAddress;
	}

}
