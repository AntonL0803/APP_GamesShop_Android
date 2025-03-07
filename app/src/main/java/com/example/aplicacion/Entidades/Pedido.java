package com.example.aplicacion.Entidades;

import java.util.List;

public class Pedido {
    private String nombre;
    private String fecha;
    private double total;
    private List<Producto> productos;

    public Pedido() {
    }

    public Pedido(String nombre, String fecha, double total, List<Producto> productos) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.total = total;
        this.productos = productos;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
