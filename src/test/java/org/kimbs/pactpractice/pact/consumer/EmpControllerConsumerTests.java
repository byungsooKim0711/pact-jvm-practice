package org.kimbs.pactpractice.pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.kimbs.pactpractice.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EmpControllerConsumerTests {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("EmpController", "localhost", 8080, this);


    @Pact(consumer = "EmpControllerClient")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/JSON");

        DslPart results = new PactDslJsonBody()
            .numberType("id", 1)
            .stringType("empName", "kimbs")
            .stringType("job", "kimbs")
            .integerType("salary", 3100)
            .asBody();

        return builder
            .given("There is an employee whose name is kimbs and whose job is kimbs and salary is 3100.")
            .uponReceiving("A request for whose name is kimbs and whose job is kimbs and salary is 3100.")
            .path("/api/emp/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(headers)
                .body(results)
            .toPact();
    }

    @Test
    @PactVerification()
    public void test1() {
        /* default directory is target/pacts */
        //System.setProperty("pact.rootDir","../pacts");

        ResponseEntity<Employee> response = new RestTemplate()
            .getForEntity(mockProvider.getUrl() + "/api/emp/{id}", Employee.class, 1);

        assertThat(response.getStatusCode().value());
        assertThat(response.getHeaders().get("Content-Type").contains("application/JSON")).isTrue();
        assertThat(response.getBody().getEmpName()).isEqualTo("kimbs");
        assertThat(response.getBody().getJob()).isEqualTo("kimbs");
        assertThat(response.getBody().getSalary()).isEqualTo(3100);
    }
}