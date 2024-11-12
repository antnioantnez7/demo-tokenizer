package mx.gob.banobras.apitokenizer.infraestructure.adapter.in.controller;


import org.springframework.http.ResponseEntity;

/**
 * TokenizerController.java:
 * 
 * Clase controller que expone los servicios Rest para validar y generar el
 * token
 * 
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since jdk 17
 */

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.TokenizerResponseDTO;

@Tag(name = "Api Tokenizer Intranet/Privado", description = "Servicio para generar y valiar tokens de los aplicativos que est&aacute;n expuestos en intranet.")
@RequestMapping("/tokenizer")
public interface ITokenizerController {
	
	
	
	/**
	 * Metodo para obtener el Token, para el consumo de los microservicios.
	 * 
	 * @param userName        - Alias del usuario.
	 * @param consumer-api-id - Nombre de la intefaz que lo va a consumir el
	 *                          microservicio.
	 * @param functional-id   - Acronimo de la funcionalidad.
	 * @param transaction-id  - Identificador de la transacción generado por UUDI.
	 * @param refresh-token   - Tiempo de duracion del refresh Token, si el valor es
	 *                          Cero, no se genera.
	 * 
	 * @return regresa el objeto TokenizerResponseDTO con los datos del Token
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 * 
	 */
	@Operation(summary = "Servicio para generar tokens, intranet", description = "Servicio para generar tokens, intranet")
	@Parameter(name = "credentials", required = true, description = "Credenciales encriptadas, usuario y password.", example = "0FFA7868B0A8CE36ED6C98230E7AC933")
	@Parameter(name = "app-name", required = true, description = "Nombre del sistema que consume el servicio.", example = "SICOVI")
	@Parameter(name = "consumer-id", required = true, description = "Capa del sistema que consuem el servicio.", example = "UI SICOVI")
	@Parameter(name = "functional-id", required = true, description = "Funcionalidad que consume el servicio.", example = "Login user")
	@Parameter(name = "transaction-id", required = true, description = "Identificador &uacute;nico para identificar la operación, funcionalidad o transacci&oacute;n, generado por c&oacute;digo UUID", example = "9680e51f-4766-4124-a3ff-02e9c3a5f9d6")
	@Parameter(name = "time-refresh-token", required = true, description = "Tiempo de duraci&oacute;n del Refresh-Token, expresado en minutos, si es igual a 0, no se genera el Refresh-Token.",  example = "0")
	
	@ApiResponse(responseCode = "200", description = "Se genera el token correctamente.")
	@ApiResponse(responseCode = "400", description = "Solicitud err&oacute;nea.")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado.")
	@ApiResponse(responseCode = "500", description = "Error Interno.")
	@ApiResponse(responseCode = "503", description = "Servicio no disponible.")
	
	@PostMapping("/v1/token")
	public ResponseEntity<TokenizerResponseDTO> getToken(
			@RequestHeader(value = "credentials") String credentials,
			@RequestHeader(value = "app-name") String appName,
			@RequestHeader(value = "consumer-id") String consumerId,
			@RequestHeader(value = "functional-id") String functionalId,
			@RequestHeader(value = "transaction-id") String transactionId,
			@RequestHeader(value = "time-refresh-token") Integer timeRefreshToken);
	
	
	
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
	@Operation(summary = "Servicio para validar tokens, intranet", description = "Servicio para validar tokens, intranet")
	@Parameter(name = "credentials", required = true, description = "Credenciales encriptadas, usuario y password.", example = "0FFA7868B0A8CE36ED6C98230E7AC933")
	@Parameter(name = "token-auth", required = true, description = "Token de autenticaci&oacute;n", example = "Bearer eyJ0eXAiOiJKV1Qi...")
	@Parameter(name = "app-name", required = true, description = "Nombre del sistema que consume el servicio.", example = "SICOVI")
	@Parameter(name = "consumer-id", required = true, description = "Capa del sistema que consuem el servicio.", example = "UI SICOVI")
	@Parameter(name = "functional-id", required = true, description = "Funcionalidad que consume el servicio.", example = "Login user")
	@Parameter(name = "transaction-id", required = true, description = "Identificador &uacute;nico para identificar la operación, funcionalidad o transacci&oacute;n, generado por c&oacute;digo UUID", example = "9680e51f-4766-4124-a3ff-02e9c3a5f9d6")
	
	@ApiResponse(responseCode = "200", description = "El token es v&aacute;lido.")
	@ApiResponse(responseCode = "400", description = "Solicitud err&oacute;nea.")
	@ApiResponse(responseCode = "404", description = "Recurso no encontrado.")
	@ApiResponse(responseCode = "500", description = "Error Interno.")
	@ApiResponse(responseCode = "503", description = "Servicio no disponible.")
	
	
	@PostMapping("/v1/valid")
	public ResponseEntity<TokenizerResponseDTO> validToken(
			@RequestHeader(value = "credentials") String credentials,
			@RequestHeader(value = "token-auth") String tokenAuth, 
			@RequestHeader(value = "app-name") String appName,
			@RequestHeader(value = "consumer-id") String consumerId,
			@RequestHeader(value = "functional-id") String functionalId,
			@RequestHeader(value = "transaction-id") String transactionId);
	
	
	
}
