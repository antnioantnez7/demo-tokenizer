package mx.gob.banobras.apitokenizer.application.service;

/**
 * TokenizerUseCaseService.java:
 * 
 * Clase de tipo @Service que contiene las funciones del caso de uso del Api TOkenizer.
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import mx.gob.banobras.apitokenizer.application.port.in.ITokenizerCasoUsoService;
import mx.gob.banobras.apitokenizer.application.port.out.ICipherClient;
import mx.gob.banobras.apitokenizer.application.port.out.ILdapClient;
import mx.gob.banobras.apitokenizer.common.util.ConstantsToken;
import mx.gob.banobras.apitokenizer.common.util.ErrorDetail;
import mx.gob.banobras.apitokenizer.dominio.model.Tokenizer;
import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.CipherResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.ErrorMessageDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.LdapResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.TokenizerResponseDTO;

@Service
public class TokenizerCasoUsoServiceImpl implements ITokenizerCasoUsoService {

	/** Trazas de la aplicación */
	Logger log = LogManager.getLogger(TokenizerCasoUsoServiceImpl.class);
	/** Variable para inejctar la clase Tokenizer */
	private final Tokenizer tokenizer;

	/** Variable para inejctar la clase ILdapApiRestClient, para conexión a LDAP */
	private final ILdapClient iLdapApiRestClient;

	/** Variable para inejctar la clase ILdapApiRestClient, para conexión a LDAP */
	private final ICipherClient iCipeherClient;

	/**
	 * Constructor para inyectar los objetos Tokenizer, ILdapOutPort,
	 * CipherAESCommon
	 * 
	 * @param Tokenizer    Objeto de dominio el Api Tokenizer.
	 * @param iLdapOutPort Interface de puerto de salida para conectarse al LDAP.
	 * @Param CipherAESCommon Componente para desencriptar datos.
	 */
	public TokenizerCasoUsoServiceImpl(Tokenizer tokenizer, ILdapClient iLdapApiRestClient,
			ICipherClient iCipeherClient) {
		this.tokenizer = tokenizer;
		this.iLdapApiRestClient = iLdapApiRestClient;
		this.iCipeherClient = iCipeherClient;
	}

	/**
	 * Metodo para crear el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del token.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;
		LdapResponseDTO ldapResponseDTO = new LdapResponseDTO();
		CipherResponseDTO cipherResponseDTO = null;

		try {

			if (tokenizerDTO.getCredentials().isEmpty()) {
				throw new IllegalArgumentException(ConstantsToken.MSG_CREDENTIALS_EMPTY.getName() + "ssssss");
			}

			/** Desencripta credenciales **/
			cipherResponseDTO = iCipeherClient.decode(tokenizerDTO);

			if (cipherResponseDTO.getStatusCode() == 200) {
				log.info("Busca el usuairo en LDAP: ");
				tokenizerDTO.setUserName(cipherResponseDTO.getDataDTO().getUserName());
				tokenizerDTO.setPassword(cipherResponseDTO.getDataDTO().getPassword());
				/** Busca el usuario que exista en LDAP */
				ldapResponseDTO = iLdapApiRestClient.authorizationLDAP(tokenizerDTO);

				/** Si encontro el usuario continua para generar el token */
				if (ldapResponseDTO.getStatusCode() == 200) {
					log.info("Inicia getToken()");
					tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);
				} else {
					ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
					errorMessageDTO.setStatusCode(ldapResponseDTO.getErrorMessageDTO().getStatusCode());
					errorMessageDTO.setTimestamp(ldapResponseDTO.getErrorMessageDTO().getTimestamp());
					errorMessageDTO.setMessage(ldapResponseDTO.getErrorMessageDTO().getMessage());
					errorMessageDTO.setDetail(ldapResponseDTO.getErrorMessageDTO().getDetail());
					/** Respuesta del servicio **/
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(ldapResponseDTO.getStatusCode());
					tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
				}
			} else {
				log.info("Credenciales incorrectas");
				ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
				errorMessageDTO.setStatusCode(cipherResponseDTO.getErrorMessageDTO().getStatusCode());
				errorMessageDTO.setTimestamp(cipherResponseDTO.getErrorMessageDTO().getTimestamp());
				errorMessageDTO.setMessage(cipherResponseDTO.getErrorMessageDTO().getMessage());
				errorMessageDTO.setDetail(cipherResponseDTO.getErrorMessageDTO().getDetail());
				/** Respuesta del servicio **/
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(cipherResponseDTO.getStatusCode());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_ERROR_500.getName());
			errorMessageDTO.setDetail(ex.getCause().getMessage() + " - " + ErrorDetail.getDetail(ex));
			/** Respuesta del servicio **/
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
		}
		log.info("Finaliza getToken()");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para validar el Token
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para validar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del token.
	 * 
	 */
	@Override
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO) {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		LdapResponseDTO ldapResponseDTO = new LdapResponseDTO();
		CipherResponseDTO cipherResponseDTO = null;

		log.info("Inica validateToken");
		try {

			/** Desencripta credenciales **/
			cipherResponseDTO = iCipeherClient.decode(tokenizerDTO);

			if (cipherResponseDTO.getStatusCode() == 200) {
				log.info("Busca el usuairo en LDAP");
				tokenizerDTO.setUserName(cipherResponseDTO.getDataDTO().getUserName());
				tokenizerDTO.setPassword(cipherResponseDTO.getDataDTO().getPassword());
				/** Busca el usuario que exista en LDAP */
				ldapResponseDTO = iLdapApiRestClient.authorizationLDAP(tokenizerDTO);

				/** Si encontro el usuario continua para validar el token */
				if ((ldapResponseDTO.getStatusCode() == 200)) {
					log.info("validaToken()");
					tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);

				} else {
					ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
					errorMessageDTO.setStatusCode(ldapResponseDTO.getErrorMessageDTO().getStatusCode());
					errorMessageDTO.setTimestamp(ldapResponseDTO.getErrorMessageDTO().getTimestamp());
					errorMessageDTO.setMessage(ldapResponseDTO.getErrorMessageDTO().getMessage());
					errorMessageDTO.setDetail(ldapResponseDTO.getErrorMessageDTO().getDetail());
					/** Respuesta del servicio **/
					tokenizerResponseDTO = new TokenizerResponseDTO();
					tokenizerResponseDTO.setStatusCode(ldapResponseDTO.getStatusCode());
					tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
				}
			} else {
				log.info("Credenciales incorrectas");
				ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
				errorMessageDTO.setStatusCode(cipherResponseDTO.getErrorMessageDTO().getStatusCode());
				errorMessageDTO.setTimestamp(cipherResponseDTO.getErrorMessageDTO().getTimestamp());
				errorMessageDTO.setMessage(cipherResponseDTO.getErrorMessageDTO().getMessage());
				errorMessageDTO.setDetail(cipherResponseDTO.getErrorMessageDTO().getDetail());
				/** Respuesta del servicio **/
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(cipherResponseDTO.getStatusCode());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_ERROR_500.getName());
			errorMessageDTO.setDetail(ex.getCause().getMessage() + " - " + ErrorDetail.getDetail(ex));
			/** Respuesta del servicio **/
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
		}
		log.info("Termina validateToken");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para crear el Token para los sistemas publicos.
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para generar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del toekn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO createTokenPublic(TokenizerDTO tokenizerDTO) {

		/** Variable que contiene el objeto de respuesta del token */
		TokenizerResponseDTO tokenizerResponseDTO = null;
		CipherResponseDTO cipherResponseDTO = null;
		try {

			log.info("Inicia crear token public");

			/** Desencripta credenciales **/
			cipherResponseDTO = iCipeherClient.decode(tokenizerDTO);

			if (cipherResponseDTO.getStatusCode() == 200) {
				tokenizerDTO.setUserName(cipherResponseDTO.getDataDTO().getUserName());
				tokenizerDTO.setPassword(cipherResponseDTO.getDataDTO().getPassword());
				tokenizerResponseDTO = tokenizer.getToken(tokenizerDTO);
			} else {
				log.info("Credenciales incorrectas");
				ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
				errorMessageDTO.setStatusCode(cipherResponseDTO.getErrorMessageDTO().getStatusCode());
				errorMessageDTO.setTimestamp(cipherResponseDTO.getErrorMessageDTO().getTimestamp());
				errorMessageDTO.setMessage(cipherResponseDTO.getErrorMessageDTO().getMessage());
				errorMessageDTO.setDetail(cipherResponseDTO.getErrorMessageDTO().getDetail());
				/** Respuesta del servicio **/
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(cipherResponseDTO.getStatusCode());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_ERROR_500.getName());
			errorMessageDTO.setDetail(ex.getCause().getMessage() + " - " + ErrorDetail.getDetail(ex));
			/** Respuesta del servicio **/
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
		}
		log.info("Finaliza crear token public");
		return tokenizerResponseDTO;
	}

	/**
	 * Metodo para validar el Token para los sistemas publicos.
	 * 
	 * @param tokenizerDTO Objeto que contien los datos para validar el token.
	 * @return TokenizerResponseDTO regresa el objeto TokenizerResponseDTO que
	 *         contiene los datos del tokrn.
	 * 
	 */
	@Override
	public TokenizerResponseDTO validateTokenPublic(TokenizerDTO tokenizerDTO) {
		TokenizerResponseDTO tokenizerResponseDTO = null;
		CipherResponseDTO cipherResponseDTO = null;
		log.info("Inica validateTokenPublic");
		try {
			/** Desencripta credenciales **/
			cipherResponseDTO = iCipeherClient.decode(tokenizerDTO);
			if (cipherResponseDTO.getStatusCode() == 200) {
				tokenizerDTO.setUserName(cipherResponseDTO.getDataDTO().getUserName());
				tokenizerDTO.setPassword(cipherResponseDTO.getDataDTO().getPassword());

				tokenizerResponseDTO = tokenizer.validaToken(tokenizerDTO);
			} else {
				log.info("Credenciales incorrectas");
				ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
				errorMessageDTO.setStatusCode(cipherResponseDTO.getErrorMessageDTO().getStatusCode());
				errorMessageDTO.setTimestamp(cipherResponseDTO.getErrorMessageDTO().getTimestamp());
				errorMessageDTO.setMessage(cipherResponseDTO.getErrorMessageDTO().getMessage());
				errorMessageDTO.setDetail(cipherResponseDTO.getErrorMessageDTO().getDetail());
				/** Respuesta del servicio **/
				tokenizerResponseDTO = new TokenizerResponseDTO();
				tokenizerResponseDTO.setStatusCode(cipherResponseDTO.getStatusCode());
				tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ConstantsToken.EXCEPTION.getName(), ex);
			ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO();
			errorMessageDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorMessageDTO.setTimestamp(new Date());
			errorMessageDTO.setMessage(ConstantsToken.MSG_ERROR_500.getName());
			errorMessageDTO.setDetail(ex.getCause().getMessage() + " - " + ErrorDetail.getDetail(ex));
			/** Respuesta del servicio **/
			tokenizerResponseDTO = new TokenizerResponseDTO();
			tokenizerResponseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			tokenizerResponseDTO.setErrorMessageDTO(errorMessageDTO);
		}
		log.info("Termina validateTokenPublic");
		return tokenizerResponseDTO;
	}


}
