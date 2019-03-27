package org.kimbs.pactpractice.pact.provider;

import org.junit.runner.RunWith;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactUrl;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;


@RunWith(PactRunner.class)
@Provider("EmpController")
@PactUrl(urls = "${your.pactfile.url}")
@VerificationReports({"console", "markdown"})
public class EmpControllerProviderTests {

    @TestTarget
    public final Target target = new HttpTarget(8080);

    @State("There is an employee whose name is kimbs and whose job is kimbs and salary is 3100.")
    public void testFindById() {
        System.out.println("There is an employee whose name is kimbs and whose job is kimbs and salary is 3100.");
    }

    @State("list")
    public void testFindAll() {
        
    }

    @State("test DELETE")
    public void testDeleteById() {

    }

    @State("test PUT")
    public void testUpdateById() {

    }

    @State("test POST")
    public void testSaveEmployee() {

    }
    
}