package com.arka.carrito_service.infrastructure.adapters.mapper;

import com.arka.carrito_service.domain.model.Producto;
import com.arka.carrito_service.infrastructure.adapters.entity.CategoriasEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.ProductosEntity;
import com.arka.carrito_service.infrastructure.adapters.entity.ProveedorEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductosEntity toEntity(Producto producto) {
        if (producto == null) return null;
        ProductosEntity productosEntity = new ProductosEntity();
        productosEntity.setNombre(producto.getNombre());
        productosEntity.setPrecio(producto.getPrecio());
        productosEntity.setStock(producto.getStock());
        productosEntity.setCaracteristicas(producto.getCaracteristicas());
        productosEntity.setMarca(producto.getMarca());
        if (producto.getProveedor()!= null){
            ProveedorEntity proveedor=new ProveedorEntity();
            proveedor.setId(producto.getProveedor());
            productosEntity.setProveedor(proveedor);
        }
        if (producto.getCategoria()!= null){
            CategoriasEntity categoriasEntity=new CategoriasEntity();
            categoriasEntity.setId(producto.getCategoria());
            productosEntity.setCategoria(categoriasEntity);
        }
        return productosEntity;
    }

    public Producto toModel(ProductosEntity productoEntity){
        if (productoEntity==null)return null;
        Producto producto=new Producto();
        producto.setId(productoEntity.getId());
        producto.setNombre(productoEntity.getNombre());
        producto.setPrecio(productoEntity.getPrecio());
        producto.setStock(productoEntity.getStock());
        producto.setCaracteristicas(productoEntity.getCaracteristicas());
        producto.setMarca(productoEntity.getMarca());
        producto.setCategoria(productoEntity.getCategoria().getId());
        producto.setProveedor(productoEntity.getProveedor().getId());

        return producto;
    }
}
