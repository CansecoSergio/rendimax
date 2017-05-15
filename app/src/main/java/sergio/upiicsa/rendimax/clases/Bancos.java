package sergio.upiicsa.rendimax.clases;

/**
 * Created by Sergio on 3/28/2017.
 */

public class Bancos {
    String appUrl;
    String logoUrl;
    String nombre;
    String paginaWeb;
    String siglas;
    Double tasaInteres;
    Double tasaRendimiento;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public Double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(Double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public Double getTasaRendimiento() {
        return tasaRendimiento;
    }

    public void setTasaRendimiento(Double tasaRendimiento) {
        this.tasaRendimiento = tasaRendimiento;
    }
}
