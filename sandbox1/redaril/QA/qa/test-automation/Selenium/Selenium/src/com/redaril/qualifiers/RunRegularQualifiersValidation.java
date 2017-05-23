package com.redaril.qualifiers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.redaril.qualifiers.validation.regular.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	RegularQualifiersValidation.class
})

public class RunRegularQualifiersValidation {

}
