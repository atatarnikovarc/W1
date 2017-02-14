package testscript;

import java.util.Vector;

public class TestScript {
	private String name; //test script name - based on filename
	private Vector steps; //each elm is TestStep elm
	private int number;
	
	public TestScript(String name, Vector steps) {
		this.name = name;
		this.steps = steps;
		this.number = steps.size();
	}
	
	public String getName() { return this.name; }
	
	public TestStep getStep(int index) {
		return (TestStep)steps.get(index);
	}
	
	public int getStepsNumber() { return this.number; }
}
