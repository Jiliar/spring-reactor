package com.jsolution.service.impl;

import com.jsolution.model.Invoice;
import com.jsolution.repository.ICustomerRepository;
import com.jsolution.repository.IDishRepository;
import com.jsolution.repository.IInvoiceRepository;
import com.jsolution.repository.IGenericRepository;
import com.jsolution.service.IInvoiceService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService{

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IDishRepository dishRepository;

    @Override
    protected IGenericRepository getRepository() {
        return invoiceRepository;
    }

    public Mono<byte[]> generateReport(String idInvoice) {
        //Obteniendo Client
        return invoiceRepository.findById(idInvoice)
                .flatMap(inv -> Mono.just(inv)
                        .zipWith(customerRepository.findById(inv.getCustomer().getId()), (in, cl) -> {
                            in.setCustomer(cl);
                            return in;
                        })
                )
                //Obteniendo Dish
                .flatMap(inv -> {
                    return Flux.fromIterable(inv.getInvoice_items())
                            .flatMap(item -> {
                                return dishRepository.findById(item.getDish().getId())
                                        .map(d -> {
                                            item.setDish(d);
                                            return item;
                                        });
                            }).collectList().flatMap(list -> {
                                inv.setInvoice_items(list);
                                return Mono.just(inv);
                            });
                })
                .map(inv -> {
                    InputStream stream;
                    try{
                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("txt_client", inv.getCustomer().getNames() + "-" + inv.getCustomer().getLastnames());

                        stream = getClass().getResourceAsStream("/facturas.jrxml");
                        JasperReport report = JasperCompileManager.compileReport(stream);
                        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(inv.getInvoice_items()));
                        return JasperExportManager.exportReportToPdf(print);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return new byte[0];
                });
    }
}
