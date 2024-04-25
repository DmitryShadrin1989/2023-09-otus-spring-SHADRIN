package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    public static final String JOB_NAME = "migrateLibraryJob";


    private final JobRepository jobRepository;

    @Bean
    public JobRegistryBeanPostProcessor postProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor processor = new JobRegistryBeanPostProcessor();
        processor.setJobRegistry(jobRegistry);
        return processor;
    }

    @Bean
    public Job migrateLibraryJob(Flow migrateAuthorsAndGenresFlow,
                                 TaskletStep createBookTempTable,
                                 TaskletStep dropBookTempTable,
                                 Step migrateBooksStep,
                                 Step migrateCommentsStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(migrateAuthorsAndGenresFlow)
                .next(createBookTempTable)
                .next(migrateBooksStep)
                .next(migrateCommentsStep)
                .next(dropBookTempTable)
                .end()
                .build();
    }

    @Bean
    public Flow migrateAuthorsAndGenresFlow(Step migrateAuthorsStep,
                                            Step migrateGenresStep) {
        return new FlowBuilder<SimpleFlow>("migrateAuthorsAndGenresFlow")
                .split(new SimpleAsyncTaskExecutor("spring_batch"))
                .add(new FlowBuilder<SimpleFlow>("migrateAuthorsFlow")
                                .start(migrateAuthorsStep)
                                .build(),
                        new FlowBuilder<SimpleFlow>("migrateGenresFlow")
                                .start(migrateGenresStep)
                                .build())
                .build();
    }
}
