package mx.gob.banobras.apitokenizer.application.port.out;

import javax.naming.NamingException;

import mx.gob.banobras.apitokenizer.dominio.model.TokenizerDTO;
import mx.gob.banobras.apitokenizer.infraestructure.adapter.in.dto.LdapResponseDTO;

/**
 * ILdapOutPort.java:
 * 
 * Interface que contiene los metodos para buscar el usuario en ldap.
 * 
 * @author Marcos Gonzalez
 * @version 1.0, 13/06/2024
 * @see documento "MAR - Marco Arquitectonico de Referencia"
 * @since JDK 17
 */
public interface ILdapClient {

	/**
	 * Metodo para buscar el usuario en LDAP, mediante el servicio rest.
	 * 
	 * @param tokenizerDTO contiene los datos de usuario y password a buscar.
	 * @return HttpResponse<String> regresa el objeto con datos, si existe el usuario.
	 * 
	 * @throws NamingException excepción durante el proceso de generar el Token.
	 */
	public LdapResponseDTO authorizationLDAP(TokenizerDTO tokenizerDTO)  throws Exception ;
	
	
}
