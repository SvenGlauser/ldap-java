package ldap;

public class Application {
    public static void main(String[] args) {
        ConnexionLDAP connexionLDAP = ConnexionLDAP.getInstance(
                "ldap://localhost:10389",
                "uid=admin, ou=system",
                "secret"
        );

        System.out.println(
                connexionLDAP
                        .addUser("Nouvel utilisateur", "CN=user, DC=example, DC=com")
                        .get("sn")
        );

        connexionLDAP.remove("CN=user, DC=example, DC=com");

        System.out.println(
                connexionLDAP
                        .getAttributes("CN=user, DC=example, DC=com")
                        .get("sn")
        );

        connexionLDAP.close();
    }
}