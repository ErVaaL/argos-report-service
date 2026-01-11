package com.erval.argos.report.adapters.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import com.erval.argos.report.adapters.mongo.model.ReportJobDocument;
import com.erval.argos.report.adapters.mongo.repositories.ReportJobMongoRepository;
import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.SortDirection;
import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class MongoReportJobRepositoryAdapterIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    private static MongoClient client;
    private static ReportJobMongoRepository repo;
    private static MongoReportJobRepositoryAdapter adapter;

    @BeforeAll
    static void setup() {
        client = MongoClients.create(mongo.getReplicaSetUrl());
        var factory = new SimpleMongoClientDatabaseFactory(client, "report-test");
        var conversions = new MongoCustomConversions(List.copyOf(Jsr310Converters.getConvertersToRegister()));
        var context = new MongoMappingContext();
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        context.afterPropertiesSet();
        var converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        converter.setCustomConversions(conversions);
        converter.afterPropertiesSet();
        var template = new MongoTemplate(factory, converter);
        var repoFactory = new MongoRepositoryFactory(template);
        repo = repoFactory.getRepository(ReportJobMongoRepository.class);
        adapter = new MongoReportJobRepositoryAdapter(repo);
    }

    @AfterAll
    static void teardown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void saveAndFindById_roundTrip() {
        var job = new ReportJob("job-1", "dev-1", "Device", ReportFormat.PDF, ReportStatus.GENERATING,
                Instant.EPOCH, Instant.EPOCH, null, null, Instant.EPOCH, Instant.EPOCH);

        adapter.save(job);
        var found = adapter.findById("job-1");

        assertTrue(found.isPresent());
        assertEquals(ReportStatus.GENERATING, found.get().status());
    }

    @Test
    void findAll_returnsPage() {
        repo.deleteAll();
        repo.save(ReportJobDocument.fromDomain(new ReportJob("job-1", "dev-1", "Device", ReportFormat.PDF,
                ReportStatus.READY, Instant.EPOCH, Instant.EPOCH, "/tmp/a.pdf", null, Instant.EPOCH, Instant.EPOCH)));
        repo.save(ReportJobDocument.fromDomain(new ReportJob("job-2", "dev-2", "Device", ReportFormat.PDF,
                ReportStatus.FAILED, Instant.EPOCH, Instant.EPOCH, null, "boom", Instant.EPOCH, Instant.EPOCH)));

        var pageRequest = new PageRequest(0, 1, "createdAt", SortDirection.DESC);
        var page = adapter.findAll(pageRequest);

        assertNotNull(page);
        assertEquals(1, page.content().size());
        assertEquals(0, page.page());
    }

    @Test
    void findAll_withoutSort_returnsPage() {
        repo.deleteAll();
        repo.save(ReportJobDocument.fromDomain(new ReportJob("job-1", "dev-1", "Device", ReportFormat.PDF,
                ReportStatus.READY, Instant.EPOCH, Instant.EPOCH, "/tmp/a.pdf", null, Instant.EPOCH, Instant.EPOCH)));

        var pageRequest = new PageRequest(0, 10, null, SortDirection.DESC);
        var page = adapter.findAll(pageRequest);

        assertNotNull(page);
        assertEquals(1, page.content().size());
    }
}
