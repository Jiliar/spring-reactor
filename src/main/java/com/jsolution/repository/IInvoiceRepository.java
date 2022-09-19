package com.jsolution.repository;

import com.jsolution.model.Invoice;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceRepository extends IGenericRepository<Invoice, String> {
}
