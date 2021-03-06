package dam.isi.frsf.utn.edu.ar.lab05.modelo;

/**
 * Created by mdominguez on 06/10/16.
 */
public class Tarea {

    private Integer id;
    private Boolean terminada;
    private Integer horasEstimadas;
    private Integer minutosTrabajados;
    private Boolean finalizada;
    private Proyecto proyecto;
    private Prioridad prioridad;
    private Usuario responsable;
    private String descripcion;
    public Tarea() {
    }
    public Tarea(Boolean terminada, Integer horasEstimadas, Integer minutosTrabajados, Boolean finalizada, Proyecto proyecto, Prioridad prioridad, Usuario responsable, String descripcion) {
        this.id = id;
        this.finalizada = terminada;
        this.horasEstimadas = horasEstimadas;
        this.minutosTrabajados = minutosTrabajados;
        this.finalizada = finalizada;
        this.proyecto = proyecto;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.descripcion = descripcion;
    }
    public Tarea(Integer id, Boolean terminada, Integer horasEstimadas, Integer minutosTrabajados, Boolean finalizada, Proyecto proyecto, Prioridad prioridad, Usuario responsable, String descripcion) {
        this.id = id;
        this.finalizada = terminada;
        this.horasEstimadas = horasEstimadas;
        this.minutosTrabajados = minutosTrabajados;
        this.finalizada = finalizada;
        this.proyecto = proyecto;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.descripcion = descripcion;
    }
    public Tarea(Integer id, Boolean terminada, Integer horasEstimadas, Integer minutosTrabajados, Proyecto proyecto, Prioridad prioridad, Usuario responsable, String descripcion) {
        this.id = id;
        this.finalizada = terminada;
        this.horasEstimadas = horasEstimadas;
        this.minutosTrabajados = minutosTrabajados;
        this.finalizada = terminada;
        this.proyecto = proyecto;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.descripcion = descripcion;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getTerminada() {
        return finalizada;
    }

    public void setTerminada(Boolean terminada) {
        this.finalizada = terminada;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public Integer getMinutosTrabajados() {
        return minutosTrabajados;
    }

    public void setMinutosTrabajados(Integer minutosTrabajados) {
        this.minutosTrabajados = minutosTrabajados;
    }


    public Boolean getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public String getDescripcion() { return this.descripcion;}
}
