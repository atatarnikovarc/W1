package com.redaril.dmptf.tests;

import com.redaril.dmptf.tests.qualifiers.validation.DataTransfer;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataTransfer.class
})
public class RunAll {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(DataTransfer.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}

