package com.jono.batchjobs.config;

import com.jono.core.dto.ClientDto;
import com.jono.core.entity.ClientEntity;
import com.jono.core.transformer.ClientTransformer;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class ClientExportJobConfig {

    @Bean
    public Job clientExportJob(final JobRepository jobRepository, final Step clientExportStep) {
        return new JobBuilder("clientExportJob", jobRepository)
                .start(clientExportStep)
                .build();
    }

    @SuppressWarnings("BoundedWildcard")
    @Bean
    public Step clientExportStep(final JobRepository jobRepository,
                                 final PlatformTransactionManager transactionManager,
                                 final ItemReader<ClientEntity> reader,
                                 final ItemProcessor<ClientEntity, ClientDto> processor,
                                 final ItemWriter<ClientDto> writer) {
        return new StepBuilder("clientExportStep", jobRepository)
                .<ClientEntity, ClientDto>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ClientEntity> reader(final EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<ClientEntity>()
                .name("clientReader")
                .entityManagerFactory(emf)
                .queryString("SELECT c FROM Client c ORDER BY c.accountNo")
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<ClientEntity, ClientDto> processor(final ClientTransformer transformer) {
        return transformer::toDto;
    }

    @Bean
    public ItemWriter<ClientDto> writer() throws IOException {
        final var csv = File.createTempFile("clients_", ".csv");
        csv.deleteOnExit();

        final var writer = new FlatFileItemWriter<ClientDto>();
        writer.setResource(new FileSystemResource(csv));
        writer.setAppendAllowed(false);

        final var fields = List.of("id", "accountNo", "name", "status");

        final var fieldExtractor = new BeanWrapperFieldExtractor<ClientDto>();
        fieldExtractor.setNames(fields.toArray(new String[0]));

        writer.setHeaderCallback(w -> w.write(String.join(",", fields)));

        final var lineAggregator = new DelimitedLineAggregator<ClientDto>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }

}
