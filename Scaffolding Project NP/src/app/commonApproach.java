package app;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPFileArg;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jmeter.engine.StandardJMeterEngine;
import java.util.Scanner;

public class commonApproach {

	public static void main(String[] args) throws Exception {

		// Load JMeter properties
		JMeterUtils.loadJMeterProperties("C:/Users/samso/OneDrive/Documents/apache-jmeter-5.6.3/bin/jmeter.properties");
		JMeterUtils.initLocale();

		StandardJMeterEngine jmeter = new StandardJMeterEngine();

		Scanner input = new Scanner(System.in);
		System.out.println("Type the Ip address of your python upload server");
		String serverIp = input.nextLine();
		System.out.println("How many requests total");
		int threadNum = input.nextInt();
		System.out.println("How long do you wnat this to take ideally in seconds");
		int rampUpTime = input.nextInt();
		input.close();
		
		// Loop Controller
		LoopController loop = new LoopController();
		loop.setLoops(1);
		loop.setFirst(true);
		loop.initialize();

		// Thread Group
		ThreadGroup tg = new ThreadGroup();
		tg.setName("Thread Group");
		tg.setNumThreads(threadNum);
		tg.setRampUp(rampUpTime);
		tg.setSamplerController(loop);

		// HTTP Sampler
		HTTPSamplerProxy http = new HTTPSamplerProxy();
		http.setName("Single File Upload");
		http.setDomain(serverIp);
		http.setPort(8000);
		http.setProtocol("http");
		http.setPath("/upload");
		http.setMethod("POST");
		http.setConnectTimeout("30000"); 
		http.setResponseTimeout("30000");

		// Add the file (correct way)
		String filePath = "C:/Users/samso/OneDrive/Desktop/Network Programming/Scaffolding/Files to Send/Entire File.json";
		HTTPFileArg file = new HTTPFileArg(filePath, "files", "application/json");
		http.setHTTPFiles(new HTTPFileArg[]{file});

		// No manual Content-Type — JMeter handles boundary & multipart MIME
		HeaderManager headers = new HeaderManager();
		http.setHeaderManager(headers);
		
		// Response Assertion
		ResponseAssertion assertion = new ResponseAssertion();
		assertion.setName("Check Status Code");
		assertion.setTestFieldResponseCode(); 
		// Status code has the match 200 exactly
		assertion.addTestString("200");
		assertion.setAssumeSuccess(false);
		assertion.setToEqualsType();
		assertion.setScopeAll();    
		
		// Test Plan
		TestPlan testPlan = new TestPlan("Single File Upload Test");
		testPlan.setUserDefinedVariables(new Arguments());
		
		// Listeners attach to the thread group or test plan
		ListedHashTree testPlanTree = new ListedHashTree();
		HashTree plan = testPlanTree.add(testPlan);
		HashTree group = plan.add(tg);
		
		// Sampler Tree
		HashTree samplerTree = group.add(http);
		samplerTree.add(assertion);

		// Summary Listener
		Summariser summariser = new Summariser("Summary Report");
		ResultCollector summaryCollector = new ResultCollector(summariser);

		// View Results Tree Listener
		ResultCollector treeCollector = new ResultCollector();
		samplerTree.add(summaryCollector);
		samplerTree.add(treeCollector);

		// Run Test
		jmeter.configure(testPlanTree);
		jmeter.run();
	}
}