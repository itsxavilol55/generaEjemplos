import java.sql.Statement;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    private static BufferedWriter writer;
    private static Connection con;

    public static void main(String[] args) {
        try {
            con = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;encrypt=true;databaseName=Registrocivil;TrustServerCertificate=true;",
                    "javi", "javi54321");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            // Crear el archivo ARFF
            writer = new BufferedWriter(new FileWriter("clientes.arff"));

            // Escribir la sección de encabezado
            writer.write("@relation Registro\n\n");
            writer.write("@attribute Estacion {Primavera, Verano, Otono, Invierno}\n");
            writer.write("@attribute year numeric\n");
            writer.write("@attribute Edad numeric\n");
            writer.write("@attribute NecesitaPermiso {Si, No}\n");
            writer.write("@attribute TienePermiso {Si, No}\n");
            writer.write("@attribute Genero {Masculino, Femenino}\n");
            writer.write("@attribute SeCaso {Si, No}\n");
            writer.write("@data\n");
            // Escribir la sección de datos
            generaDatos();
            // Cerrar el archivo
            writer.close();

            System.out.println("Archivo ARFF generado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generaDatos() {
        String[] estaciones = { "Primavera", "Verano", "Otono", "Invierno" };
        for (int i = 0; i < 2000; i++) {
            StringBuilder ejemplo = new StringBuilder();
            double num = Math.random();
            int edad = (int) ((Math.random() * 51) + 14);
            ejemplo.append(estaciones[(int) (Math.random() * 4)] + ", ");
            ejemplo.append("" + (int) ((Math.random() * 3) + 2020) + ", ");
            ejemplo.append("" + edad + ", ");
            if (num <= 0.98)// se establece el margen de error
                ejemplo.append((edad < 18 ? "Si," + (Math.random() >= 0.5 ? "Si" : "No") : "No, No") + ", ");
            // si la edad es menor a 18 necesita permiso
            else// en caso de que la persona no ocupe permiso se le asigna no tiene permiso
            {// caso de falla o corrupcion
                ejemplo.append((Math.random() >= 0.5 ? "Si" : "No") + ", ");
                ejemplo.append((Math.random() >= 0.5 ? "Si" : "No") + ", ");
            }
            ejemplo.append((Math.random() >= 0.5 ? "Masculino" : "Femenino") + ", ");
            ejemplo.append(Math.random() >= 0.5 ? "Si" : "No");
            insertaTabla(ejemplo);
            ejemplo.append("\n");
            insertaARFF(ejemplo);
        }
    }

    private static void insertaTabla(StringBuilder ejemplo) {

        ejemplo.replace(0, ejemplo.length(),
                ejemplo.toString().replaceAll("([a-zA-Z]+)", "'$1'"));
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into Personas values(" + ejemplo + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertaARFF(StringBuilder ejemplo) {
        try {
            writer.write("" + ejemplo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}