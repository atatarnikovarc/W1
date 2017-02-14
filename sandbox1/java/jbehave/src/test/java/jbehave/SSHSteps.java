package jbehave;

import static org.junit.Assert.*;
import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.junit.Assert;

public class SSHSteps {
    @Given("Check $host can execute $command command using $user and $passwd credentials and contains $expected")
    public void checkSshCommand(String host, String user, String command, String passwd, String expected) {
        String response = Util.executeSSHCommand(host, user, passwd, command);

        Assert.assertTrue("Response doesn't contain expected string", response.contains(expected));
    }
}
