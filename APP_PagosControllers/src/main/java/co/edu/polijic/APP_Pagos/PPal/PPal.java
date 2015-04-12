/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos.PPal;

import co.edu.polijic.APP_Pagos.TarjetaJpaController;
import co.edu.polijic.APP_Pagos.TransaccionJpaController;
import co.edu.polijic.app_pagos.model.RegistroTransaccion;
import co.edu.polijic.app_pagos.model.Tarjeta;
import co.edu.polijic.app_pagos.model.TipoPago;
import co.edu.polijic.app_pagos.model.Transaccion;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author felipe
 */
public class PPal {

    public static void main(String[] args) {
        /**
         * **********Instancio Persistencia******************
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("APP_PagosPU");
        /**
         * **********Crear Transaccion******************
         */
        TipoPago tipopago = new TipoPago();
        tipopago.setCdtipopago(1);
        tipopago.setDsdescripcion("Credito");
        
        Transaccion transaccion = new Transaccion();
        transaccion.setCdtransaccion(1);
        transaccion.setCdtipopago(tipopago);
        transaccion.setVltransaccion(BigDecimal.valueOf(25000,00));
        transaccion.setNmcuotaspago(2);
        
        Tarjeta tarjetaOrigen = new Tarjeta();
        Tarjeta tarjetaDestino = new Tarjeta();
        
        tarjetaOrigen.setCdtarjeta(1);
        tarjetaOrigen.setCdtransaccion(transaccion);
        tarjetaOrigen.setOpfranquicia("Visa");
        tarjetaOrigen.setOpestadotarjeta("Activo");
        tarjetaOrigen.setOptipotarjeta("Credito");
        tarjetaOrigen.setNmtarjeta(53434343);
        tarjetaOrigen.setDsmesvencimiento("04");
        tarjetaOrigen.setDsaniovencimiento("2016");
        tarjetaOrigen.setCdseguridad(21312321);
        tarjetaOrigen.setDsnombretitular("Luis ALfonso Delgado");
        
        tarjetaDestino.setCdtarjeta(2);
        tarjetaDestino.setCdtransaccion(transaccion);
        tarjetaDestino.setOpfranquicia("Visa");
        tarjetaDestino.setOpestadotarjeta("Activo");
        tarjetaDestino.setOptipotarjeta("Credito");
        tarjetaDestino.setNmtarjeta(53434343);
        tarjetaDestino.setDsmesvencimiento("02");
        tarjetaDestino.setDsaniovencimiento("2017");
        tarjetaDestino.setCdseguridad(53434);
        tarjetaDestino.setDsnombretitular("Alberto Delgado");
        
        transaccion.setCdtarjetadestino(tarjetaOrigen.getCdtarjeta());
        transaccion.setCdtarjetadestino(tarjetaDestino.getCdtarjeta());
        
                
        
        RegistroTransaccion registrotransaccion = new RegistroTransaccion();
        registrotransaccion.setCdregistro(1);
        registrotransaccion.setCdtransaccion(transaccion);
        registrotransaccion.setOpestado("Activo");
        registrotransaccion.setFefechatransaccion(Date.valueOf("2015-04-10"));
        registrotransaccion.setDsobservaciones("ninguna");
        
        TransaccionJpaController transaccionController= new TransaccionJpaController(emf);
        try {
            transaccionController.create(transaccion);
            
            
            /*CustomerJpaController customerJpaController = new CustomerJpaController(emf);
            for (Customer customer : customerJpaController.getAllCustomers()) {
            System.out.println(customer);
            }
            System.out.println("Listar los primeros 5");
            for (Customer customer : customerJpaController.getFirstFiveCustomers()) {
            System.out.println(customer);
            }
            System.out.println("Listar los primeros 5 con codigos de descuento L o H");
            for (Customer customer : customerJpaController.getCustomersSpecialDicountCodes()) {
            System.out.println(customer);
            }
            
            System.out.println("Listar los primeros 5 con codigos de descuento L o H");
            for (Object[] customer : customerJpaController.getTopTen()) {
            System.out.println(Arrays.toString(customer));
            }*/
        } catch (Exception ex) {
            Logger.getLogger(PPal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
