package ldap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * Classe permettant de gérer une connexion LDAP
 */
public class ConnexionLDAP {
    private static ConnexionLDAP instance = null;

    private final DirContext connexion;

    /**
     * Instancier une nouvelle connexion LDAP
     * @param connexion Connexion à stocker
     */
    private ConnexionLDAP(DirContext connexion) {
        this.connexion = connexion;
    }
    // Le reste de la classe

    /**
     * Récupérer la connexion actuelle ou créer une nouvelle connexion
     * @param url Url du service LDAP
     * @param user Utilisateur
     * @param password Mot de passe
     * @return Une connexion LDAP
     */
    public static ConnexionLDAP getInstance(String url, String user, String password) {
        if (instance == null) {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_PRINCIPAL, user);
            env.put(Context.SECURITY_CREDENTIALS, password);

            try {
                instance = new ConnexionLDAP(new InitialDirContext(env));
            } catch (NamingException e) {
                error("Impossible de créé la connexion");
            }
        }
        return instance;
    }

    /**
     * Récupérer les attributs d'un objet
     * @param distinguishedName DistinguishedName de l'objet
     * @return Les attributs de l'objet
     */
    public Attributes getAttributes(String distinguishedName) {
        try {
            return connexion.getAttributes(distinguishedName);
        } catch (NamingException e) {
            error("Impossible de récupérer les informations de l'object : " + distinguishedName);
        }
        return null;
    }

    /**
     * Ajouter un utilisateur
     * @param userName Nom de l'utilisateur
     * @param distinguishedName DistinguishedName de l'utilisateur
     * @return Les attributs de l'utilisateur créé
     */
    public Attributes addUser(String userName, String distinguishedName) {
        try {
            Attributes user = new BasicAttributes();
            user.put("objectClass", "inetOrgPerson");
            user.put("sn", userName);
            connexion.createSubcontext(distinguishedName, user);
            return getAttributes(distinguishedName);
        } catch (NamingException e) {
            error("Impossible de créer l'utilisateur : " + userName + " à l'emplacement : " + distinguishedName);
        }
        return null;
    }

    /**
     * Supprimer un objet
     * @param distinguishedName DistinguishedName de l'objet
     */
    public void remove(String distinguishedName) {
        try {
            connexion.destroySubcontext(distinguishedName);
        } catch (NamingException e) {
            error("Impossible de supprimer l'objet : " + distinguishedName);
        }
    }

    /**
     * Fermer une connexion
     */
    public void close() {
        try {
            connexion.close();
            instance = null;
        } catch (NamingException e) {
            error("Impossible de fermer la connexion");
        }
    }

    /**
     * Fermer le programme avec un message d'erreur
     * @param message Message d'erreur
     */
    private static void error(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
