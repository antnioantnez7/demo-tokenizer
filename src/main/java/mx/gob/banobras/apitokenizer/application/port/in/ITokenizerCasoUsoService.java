package mx.gob.banobras.apitokenizer.application.port.in;

import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.TokenizerResponseDTO;

public interface ITokenizerCasoUsoService {

	/**
	 * Metodo para crar el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para generar el token.
	 * @return regresa el objeto con el token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO createToken(TokenizerDTO tokenizerDTO);
	
		
	/**
	 * Metodo para validar el Token, para el consumo de los microservicios.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para validar el token.
	 * @return regresa el objeto TokenizerResponseDTO con los datos del token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO validateToken(TokenizerDTO tokenizerDTO);
	
	/**
	 * Metodo para obtener el Token de maner publica, para el consumo de los microservicios
	 * que estan expuestos a internet.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para generar el token.
	 * @return regresa el objeto con el token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO createTokenPublic(TokenizerDTO tokenizerDTO);
	
	/**
	 * Metodo para validar el Token de manera publica, para el consumo de los microservicios.
	 * que estan expuestos a internet.
	 * 
	 * @param tokenizerDTO - DTO que contien los datos para validar el token.
	 * @return regresa el objeto TokenizerResponseDTO con los datos del token.
	 * @throws Exception Excepción durante el proceso de generar el Token.
	 */
	public TokenizerResponseDTO validateTokenPublic(TokenizerDTO tokenizerDTO);
	
	
}
