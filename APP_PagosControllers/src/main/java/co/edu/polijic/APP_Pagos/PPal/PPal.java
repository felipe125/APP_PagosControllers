/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos.PPal;

import java.util.Arrays;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author felipe
 */
public class PPal {
    
    /************************************************/
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("APP_PagosPU");
        
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
    
    
}
