package com.erval.argos.report.adapters.mongo.repositories;

import com.erval.argos.report.adapters.mongo.model.ReportJobDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportJobMongoRepository extends MongoRepository<ReportJobDocument, String> {

}
