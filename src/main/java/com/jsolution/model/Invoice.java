package com.jsolution.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "invoices")
public class Invoice {

    @EqualsAndHashCode.Include
    @Id
    private String id;
    @Field(name = "customer")
    private Customer customer;
    @Field(name = "user")
    private User user_create;
    @Field(name = "description")
    private String description;
    @Field(name = "observation")
    private String observation;
    @Field(name = "date_create")
    private LocalDate date_create;
    @Field(name = "invoice_items")
    List<InvoiceDetail> invoice_items;
}
