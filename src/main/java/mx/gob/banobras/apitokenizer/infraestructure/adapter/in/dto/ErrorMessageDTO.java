package mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto;

/**
 * ErrorMessageDTO.java:
 * 
 * Objeto que contiene los datos para el mensaje de error. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessageDTO {
	  private int statusCode;
	  private Date timestamp;
	  private String message;
	  private String detail;
	
	  public ErrorMessageDTO(int statusCode, Date timestamp, String message, String detail) {
		super();
		this.statusCode = statusCode;
		this.timestamp = timestamp;
		this.message = message;
		this.detail = detail;
	}
	public ErrorMessageDTO(int statusCode, Date timestamp, String message) {
		super();
		this.statusCode = statusCode;
		this.timestamp = timestamp;
		this.message = message;
		
	}

	  
	  
	  
}
