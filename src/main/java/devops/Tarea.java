package devops;

public class Tarea {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipo;

    public Tarea(int id, String nombre, String descripcion, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return "[" + tipo + "] " + nombre;
    }
}