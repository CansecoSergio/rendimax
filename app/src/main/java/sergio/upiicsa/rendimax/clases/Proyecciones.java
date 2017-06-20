package sergio.upiicsa.rendimax.clases;

import java.util.Date;

/**
 * Created by Sergio on 5/26/2017.
 */

public class Proyecciones {
    String email;
    String nombre;
    Double cantidadInicial;
    Double catidadAnual;
    Integer anios;
    String banco;
    String llave;
    Date fechaProyeccion;

    public Proyecciones(String email, String nombre, Double cantidadInicial, Double cantidadAnual,
                        Integer anios, String banco, String llave, Date fechaProyeccion) {
        this.email = email;
        this.nombre = nombre;
        this.cantidadInicial = cantidadInicial;
        this.catidadAnual = cantidadAnual;
        this.anios = anios;
        this.banco = banco;
        this.llave = llave;
        this.fechaProyeccion = fechaProyeccion;
    }

    public Proyecciones() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(Double cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public Double getCatidadAnual() {
        return catidadAnual;
    }

    public void setCatidadAnual(Double catidadAnual) {
        this.catidadAnual = catidadAnual;
    }

    public Integer getAnios() {
        return anios;
    }

    public void setAnios(Integer anios) {
        this.anios = anios;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public Date getFechaProyeccion() {
        return fechaProyeccion;
    }

    public void setFechaProyeccion(Date fechaProyeccion) {
        this.fechaProyeccion = fechaProyeccion;
    }
}
