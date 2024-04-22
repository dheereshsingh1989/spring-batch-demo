package com.dheeresh.batchprocessingdemo.config;

import com.dheeresh.batchprocessingdemo.entity.Customer;
import com.dheeresh.batchprocessingdemo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    // Here we will inject two class for Job and Step.
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CustomerRepository customerRepository;


    @Bean   // 1.  Configuring the reader object
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resource/customers-10000.csv"));
        itemReader.setName("CSVReader");
        itemReader.setLinesToSkip(1); // skipping the headers of CSV file.
        itemReader.setLineMapper(lineMapper());
        return itemReader;  // this will return to ItemProcessor Class
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        // this line tokenizer will read this CSV file
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("Id", "Customer Id", "First Name", "Last Name",
                "Company", "City", "Country",
                "Phone", "Email", "Subscription Date", "Website");

        // we need to map this information to Customer Object, Hence BeanWrapperFieldSetMapper will do the same.
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        //Now both the object we need to set to LineMapper
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean   //2. Configuring the Processor Object
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }


    @Bean   //3. Configuring the Writer Object
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");   // this is a repository built in method
        return writer;
    }

    @Bean //4. Configuring the Step
    public Step step1() {
        return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean //5. Configuring the Step, After this we need to pass this job to JobLauncher in our Controller
    public Job runJob(){
        return jobBuilderFactory.get("importCustomers")
                .flow(step1())
                .end().build();
    }

}
