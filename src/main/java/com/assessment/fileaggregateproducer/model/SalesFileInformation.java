package com.assessment.fileaggregateproducer.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class SalesFileInformation {
    private String fileName;
    private Date readTime;
    private List<SaleInformation> data;
}
