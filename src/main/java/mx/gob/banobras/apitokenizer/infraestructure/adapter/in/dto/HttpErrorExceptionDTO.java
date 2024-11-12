package mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto;
/**
 * HttpErrorExceptionDTO.java:
 * 
 * Objeto que contiene los datos para el mensaje de error de http. 
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpErrorExceptionDTO {

	private String status;
	private String trace;
	private String error;
	private String path;
	
	
}
