package com.redaril.qualifiers;

//import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
//2

import com.redaril.qualifiers.builder.*;
import com.redaril.qualifiers.validation.regular.*;
import com.redaril.qualifiers.validation.statistical.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	QualifiersBuilder.class,
	RegularQualifiersValidation.class,
	StatisticalQualifiersValidation.class
})

public class QualifiersRunAll {
//	TestSuite suite; 
//	
//	public void suite() {
//		this.suite.addTestSuite(testClass);
//	}
//
}
