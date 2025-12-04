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
import org.apache.jmeter.control.IfController;
import java.util.Scanner;

public class myApproach {

	public static void main(String[] args) throws Exception {

		// Load JMeter properties replace with path to your jmeter.properties
		JMeterUtils.loadJMeterProperties("C:/Users/samso/OneDrive/Documents/apache-jmeter-5.6.3/bin/jmeter.properties");
		JMeterUtils.initLocale();
		StandardJMeterEngine jmeter = new StandardJMeterEngine();
		Scanner input = new Scanner(System.in);
		System.out.println("Type the Ip address of your server");
		String serverIp = input.nextLine();
		System.out.println("How many requests total");
		int threadNum = input.nextInt();
		System.out.println("How long do you want this to take in seconds");
		int rampUpTime = input.nextInt();
		input.close();
		
		System.out.println("Warning with my appraoch it sends 2 files one after another per each user so you will get double the amount of requests in your results.");
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
		
		// HTTP Sampler priority File 
		HTTPSamplerProxy http = new HTTPSamplerProxy();
		http.setName("Priority File Upload");
		http.setDomain(serverIp);
		http.setPort(8000);
		http.setProtocol("http");
		http.setPath("/upload");
		http.setMethod("POST");
		http.setConnectTimeout("30000"); 
		http.setResponseTimeout("30000");

		// Add the file put the file path to your Priority.json file
		String filePath = "Files/Priority.json";
		HTTPFileArg file = new HTTPFileArg(filePath, "files", "application/json");
		http.setHTTPFiles(new HTTPFileArg[]{file});

		// No manual Content-Type — JMeter handles boundary & multipart MIME
		HeaderManager headers = new HeaderManager();
		http.setHeaderManager(headers);

		// Response Assertion
		ResponseAssertion assertion = new ResponseAssertion();
		assertion.setName("Check Status Code");
		assertion.setTestFieldResponseCode(); 
		// has to exactly match 200
		assertion.addTestString("200");       
		assertion.setAssumeSuccess(false);
		assertion.setToEqualsType(); 
		assertion.setScopeAll();    
		
		// IF Controller to check if important file has been received 
		IfController ifController = new IfController();
		ifController.setCondition("${JMeterThread.last_sample_ok}");
		ifController.setEvaluateAll(false);  // Stops evaluating children once false
		ifController.setUseExpression(true); // Treat the string as an expression
		
		// HTTP Sampler Remaining Data file
		HTTPSamplerProxy remainData = new HTTPSamplerProxy();
		remainData.setName("Remaining Data Upload");
		remainData.setDomain(serverIp);
		remainData.setPort(8000);
		remainData.setProtocol("http");
		remainData.setPath("/upload");
		remainData.setMethod("POST");
		remainData.setConnectTimeout("30000"); 
		remainData.setResponseTimeout("30000");

		// Add the file
		String remFilePath = "Files/Remaining.json";
		HTTPFileArg remFile = new HTTPFileArg(remFilePath, "files", "application/json");
		remainData.setHTTPFiles(new HTTPFileArg[]{remFile});
		
		// Test Plan
		TestPlan testPlan = new TestPlan("Single File Upload Test");
		testPlan.setUserDefinedVariables(new Arguments());

		// Listeners attach to the thread group or test plan
		ListedHashTree testPlanTree = new ListedHashTree();
		HashTree plan = testPlanTree.add(testPlan);
		HashTree group = plan.add(tg);
		
		HashTree samplerTree = group.add(http);
		samplerTree.add(assertion);
		
		// Hash Tree for ifController and remaining data HTTP Post Request
		HashTree ifTree = group.add(ifController);
		ifTree.add(remainData);
		
		// Summary Listener
		Summariser summariser = new Summariser("Summary Report");
		ResultCollector summaryCollector = new ResultCollector(summariser);
		
		// View Results Tree Listener
		ResultCollector treeCollector = new ResultCollector();
		
		group.add(summaryCollector);
		group.add(treeCollector);
		
		System.out.println("Sending your requests to the server with the IP " + serverIp + " on port 8000 now");

		// Run Test
		jmeter.configure(testPlanTree);
		jmeter.run();
	}
}