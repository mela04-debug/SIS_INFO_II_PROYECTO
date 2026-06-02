package devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Configuración exacta basada en tu pantalla de conexión:
    // Servidor: localhost
    // Base de datos: devops_suite
    // trustServerCertificate=true (para activar el "Certificado de servidor de confianza" que marcaste en la foto)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=devops_suite;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa"; 
    
    // IMPORTANTE: Coloca aquí entre las comillas la contraseña real que corresponde a los puntitos de tu foto
    private static final String PASSWORD = "123456"; 

    public static Connection getConexion() throws SQLException {
        try {
            // Registrar el driver de Microsoft para Java
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("¡Error! No se encontró el Driver de SQL Server en el proyecto.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}