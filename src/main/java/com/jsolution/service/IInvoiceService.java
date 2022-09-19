package com.jsolution.service;

import com.jsolution.model.Invoice;
import reactor.core.publisher.Mono;

public interface IInvoiceService extends ICRUD<Invoice, String> {
    Mono<byte[]> generateReport(String id);
}
