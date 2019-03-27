package org.kimbs.pactpractice.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.kimbs.pactpractice.domain.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EmpControllerConsumerTests {

	@Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("EmpController", "localhost", 8080, this);

    private static Map<String, String> headers;
    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void setup() {
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        objectMapper = new ObjectMapper();
    }

    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact pactTestFindById(PactDslWithProvider builder) {

        DslPart result1 = new PactDslJsonBody()
            .numberType("id", 1)
            .stringType("empName", "kimbs")
            .stringType("job", "kimbs")
            .integerType("salary", 3100)
            .asBody();

        DslPart result2 = new PactDslJsonBody()
            .numberType("id", 2)
            .stringType("empName", "test")
            .stringType("job", "test")
            .integerType("salary", 999)
            .asBody();

        return builder.given("There is an employee whose name is kimbs and whose job is kimbs and salary is 3100.")
                .uponReceiving("A request for whose name is kimbs and whose job is kimbs and salary is 3100.")
                    .path("/api/emp/1")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body(result1)
                .uponReceiving("second test interaction")
                    .path("/api/emp/2")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body(result2)
                .toPact();
    }

    @Test
    @PactVerification(fragment = "pactTestFindById")
    public void test1() {
        /* default directory is target/pacts */
        // System.setProperty("pact.rootDir","../pacts");

        ResponseEntity<Employee> response1 = new RestTemplate()
            .getForEntity(
                mockProvider.getUrl() + "/api/emp/{id}",
                Employee.class, 
                1
            );

                
        assertThat(response1.getStatusCode().value()).isEqualTo(200);
        assertThat(response1.getHeaders().get("Content-Type").contains("application/json")).isTrue();
        assertThat(response1.getBody().getEmpName()).isEqualTo("kimbs");
        assertThat(response1.getBody().getJob()).isEqualTo("kimbs");
        assertThat(response1.getBody().getSalary()).isEqualTo(3100);
        
        ResponseEntity<Employee> response2 = new RestTemplate()
            .getForEntity(
                mockProvider.getUrl() + "/api/emp/{id}",
                Employee.class, 
                2
            );
        assertThat(response2.getStatusCode().value()).isEqualTo(200);
        assertThat(response2.getHeaders().get("Content-Type").contains("application/json")).isTrue();
        assertThat(response2.getBody().getEmpName()).isEqualTo("test");
        assertThat(response2.getBody().getJob()).isEqualTo("test");
        assertThat(response2.getBody().getSalary()).isEqualTo(999);
    }

    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact pactTestFindAll(PactDslWithProvider builder) throws JsonProcessingException {
        Employee e1 = new Employee();
        e1.setId(1L);
        e1.setEmpName("kimbs");
        e1.setJob("kimbs");
        e1.setSalary(3100);

        Employee e2 = new Employee();
        e2.setId(2L);
        e2.setEmpName("test");
        e2.setJob("test");
        e2.setSalary(999);

        List<Employee> results = new ArrayList<>();
        results.add(e1);
        results.add(e2);

        return builder
            .given("list")
            .uponReceiving("list")
                .path("/api/emp")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(objectMapper.writeValueAsString(results))
            .toPact();
    }

    @Test
    @PactVerification(fragment = "pactTestFindAll")
    public void test2() throws Exception {
        ResponseEntity<List<Employee>> response1 = new RestTemplate()
            .exchange(
                mockProvider.getUrl() + "/api/emp", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Employee>>() {}
            );

        assertThat(response1.getStatusCode().value()).isEqualTo(200);
        assertThat(response1.getHeaders().get("Content-Type").contains("application/json")).isTrue();

        assertThat(response1.getBody().get(0).getEmpName()).isEqualTo("kimbs");
        assertThat(response1.getBody().get(0).getJob()).isEqualTo("kimbs");
        assertThat(response1.getBody().get(0).getSalary()).isEqualTo(3100);

        assertThat(response1.getBody().get(1).getEmpName()).isEqualTo("test");
        assertThat(response1.getBody().get(1).getJob()).isEqualTo("test");
        assertThat(response1.getBody().get(1).getSalary()).isEqualTo(999);

    }

    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact pactTestSave(PactDslWithProvider builder) {
        DslPart result1 = new PactDslJsonBody()
            .numberType("id", 1)
            .stringType("empName", "kimbs")
            .stringType("job", "kimbs")
            .integerType("salary", 3100)
            .asBody();

        return builder
            .given("test POST")
            .uponReceiving("REQUEST POST")
                .path("/api/emp")
                .method("POST")
                .body(result1)
            .willRespondWith()
                .status(201)
                .headers(headers)
                .body(result1)
            .toPact();
    }

    @Test
    @PactVerification(fragment = "pactTestSave")
    public void test3() {
        Employee e1 = new Employee();
        e1.setId(1L);
        e1.setEmpName("kimbs");
        e1.setJob("kimbs");
        e1.setSalary(3100);
        ResponseEntity<Employee> response = new RestTemplate()
            .postForEntity(
                mockProvider.getUrl() + "/api/emp", 
                e1, 
                Employee.class
            );

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();

        assertThat(response.getBody().getEmpName()).isEqualTo("kimbs");
        assertThat(response.getBody().getJob()).isEqualTo("kimbs");
        assertThat(response.getBody().getSalary()).isEqualTo(3100);
    }

    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact pactTestDeleteById(PactDslWithProvider builder) {
        return builder
            .given("test DELETE")
            .uponReceiving("REQUEST DELETE")
                .path("/api/emp/1")
                .method("DELETE")
            .willRespondWith()
                .status(200)
            .toPact();
    }

    @Test
    @PactVerification(fragment = "pactTestDeleteById")
    public void test4() {
        ResponseEntity<Void> response = new RestTemplate()
            .exchange(
                mockProvider.getUrl() + "/api/emp/{id}", 
                HttpMethod.DELETE, 
                null, 
                Void.class, 
                1
            );
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact pactTestUpdateById(PactDslWithProvider builder) {

        DslPart result = new PactDslJsonBody()
            .numberType("id", 1)
            .stringType("empName", "test001")
            .stringType("job", "test001")
            .integerType("salary", 1234)
            .asBody();

        return builder
            .given("test PUT")
            .uponReceiving("REQUEST PUT")
                .path("/api/emp/1")
                .method("PUT")
                .body(result)
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(result)
            .toPact();
    }

    @Test
    @PactVerification(fragment = "pactTestUpdateById")
    public void test5() {
        Employee e1 = new Employee();
        e1.setId(1L);
        e1.setEmpName("test001");
        e1.setJob("test001");
        e1.setSalary(1234);
        HttpEntity<Employee> entity = new HttpEntity<>(e1);

        ResponseEntity<Employee> response = new RestTemplate()
            .exchange(
                mockProvider.getUrl() + "/api/emp/{id}", 
                HttpMethod.PUT, 
                entity,
                Employee.class,
                1
            );
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();

        assertThat(response.getBody().getEmpName()).isEqualTo("test001");
        assertThat(response.getBody().getJob()).isEqualTo("test001");
        assertThat(response.getBody().getSalary()).isEqualTo(1234);
    }
}