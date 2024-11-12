package mx.gob.banobras.apitokenizer.common.util;


/**
 * ErrorDetail.java:
 * 
 * Clase para mostrar el detalle de un error.
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see Documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */
public class ErrorDetail {

	/**
	 * Constructor de la clase.
	 */
	private ErrorDetail() {
		throw new IllegalStateException("Error Utility class");
	}

	/**
	 * Metodo para obtner el detalle del error.
	 * @param exception 
	 * @return String con el detalle del error.
	 */
	public static String getDetail(Exception exception) {
		StringBuilder exceptionDetail = new StringBuilder();
		exceptionDetail.append(exception.getLocalizedMessage());
		/** contador para el numero de mensajes a regresar **/
		int count = 0;
		for (StackTraceElement s : exception.getStackTrace()) {
			exceptionDetail.append(" - ").append(s.toString());
			if(count++ == 10) {
				break;
			}
		}
		return exceptionDetail.toString();
	}


}
