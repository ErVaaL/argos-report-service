package com.erval.argos.report.adapters.mongo.repositories;

import com.erval.argos.report.adapters.mongo.model.ReportJobDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data repository for report job documents.
 */
public interface ReportJobMongoRepository extends MongoRepository<ReportJobDocument, String> {

}
