package mx.gob.banobras.apitokenizer.common.util;


/**
 * CommonConstant.java:
 * 
 * Clase ENUM que contiene mensajes y variables de uso comun
 *  
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */

public enum ConstantsToken {
	/** Constante para el mensaje de erorr 500 */
	MSG_ERROR_500 ("Error interno, comunicarse con el administrador del sistema."),
	/** Constante para el mensaje de erorr 500 */
	MSG_ERROR_503 ("Error interno, servicio no disponible."),
	/** Constante para el mensaje de erorr 500 */
	MSG_CREDENTIALS_INVALID ("Credenciales incorrectas."),
	/** Constante para el mensaje de erorr 500 */
	MSG_CREDENTIALS_EMPTY ("No hay datos en credenciales."),
	/** COnstante para validar el localhost IPV4 */
	LOCALHOST_IPV4 ("127.0.0.1"),
	/** COnstante para validar el localhost IPV6 */
	LOCALHOST_IPV6 ("0:0:0:0:0:0:0:1"),
	/** Constante para validar la cadena "unknown" */
	UNKNOWN ("unknown"),
	/** Constante para validar el header "X-Forwarded-For" */
	H_X_FORWARDED_FOR ("X-Forwarded-For"),
	/** Constante para validar el header "Proxy-Client-IP" */
	H_PROXY_CLIENT_IP ("Proxy-Client-IP"),
	/** Constante para validar el header "WL-Proxy-Client-IP" */
	H_WL_PROXY_CLIENT_IP ("WL-Proxy-Client-IP"),
	/** Constante para validar el caracter coma "," */
	COMMA (","),
	/** Constante para imprimir en log */
	BUSCA_POR ("Busca por:: "),
	/** Constante para imprimir en log */
	ILLEGAL_ARG_EXCEPTION ("llegalArgumentException"),
	/** Constante para imprimir en log */
	NULL_POINTER_EXCEPTION ( "NullPointerException"),
	/** Constante para imprimir en log */
	EXCEPTION ("Exception"),
	/** Constante para imprimir en log */
	COMMUNICATION_EXCEPTION_LDAP ("CommunicationException - No hay conexion a LDAP"),
	/** Constante para imprimir en log */
	MSG_NO_SERVICE_LDAP ("Servicio no disponible - Service Auth LDAP."),
	/** Constante para imprimir en log */
	MSG_NO_SERVICE_DECODE ("Servicio no disponible - Service Auth Decode."),
	/** Constante para imprimir en log */
	EXPIRED_JWT_EXCEPTION ("ExpiredJwtException"),
	/** Constante para imprimir en log */
	MALFORMED_JWT_EXCEPTION ("MalformedJwtException"),
	/** Constante para el mensaje de token expirado */
	MSG_TOKEN_EXPIRED ("Token expirado."),
	/** Constante para el mensaje de token incorrecto */
	MSG_TOKEN_INCORRECTO ("Token incorrecto."),
	/** Constante para el mensaje de token no coincide con los headers */
	MSG_TOKEN_NO_MACTH_HEADERS ("El token no coincide con los Headers."),
	/** Constante SSL */
	SSL ("SSL"),
	/** Constante HTTPS */
	HTTPS ("HTTPS"),
	
	
	;
	
	private String name;

	private ConstantsToken(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
