package mx.gob.banobras.apitokenizer.application.port.out;
/**
 * ICihperClient.java:
 * 
 * Interface que contiene los metodos para encriptar datos.
 * 
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */


import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.CipherResponseDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.DataDTO;


public interface ICipherClient {

	/**
	 * Metodo para descriptar los datos de entrada.
	 * 
	 * @param dataDTO contiene los datos encrptados.
	 * @return regresa el objeto CipherResponseDTO con los datos desencriptados.
	 * 
	 * @throws Exception excepción durante el proceso desencriptar.
	 */
	public CipherResponseDTO decode(TokenizerDTO tokenizerDTO) 
			throws Exception;



}
