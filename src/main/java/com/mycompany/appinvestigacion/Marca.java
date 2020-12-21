
package com.mycompany.appinvestigacion;

/**
 *
 * @author lucho
 */
public class Marca {
    
 private Integer Codigo;
 private String Descripcion;

    public Marca(Integer Codigo, String Descripcion) {
        this.Codigo = Codigo;
        this.Descripcion = Descripcion;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public Integer getCodigo() {
        return Codigo;
    }

    public void setCodigo(Integer Codigo) {
        this.Codigo = Codigo;
    }

    
}
