package mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto;

/**
 * LdapResponseDTO.java:
 * 
 * Objeto que contiene los datos de respuesta de la autenticacion. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */
import lombok.Data;

@Data
public class LdapResponseDTO {
	private Integer statusCode;
	private LdapDTO ldapDTO;
	private ErrorMessageDTO errorMessageDTO;
}


