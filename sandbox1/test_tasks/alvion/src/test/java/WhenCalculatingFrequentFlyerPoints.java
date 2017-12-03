import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by atatarnikov on 02.07.17.
 */
@RunWith(SerenityRunner.class)
public class WhenCalculatingFrequentFlyerPoints {

    @Steps
    TravellerSteps travellerSteps;

    @Test
    @Title("Check that the Title works")
    public void shouldCalculatePointsBasedOnDistance() {
        // GIVEN
        travellerSteps.a_traveller_has_a_frequent_flyer_account_with_balance(10000);

        // WHEN
        travellerSteps.the_traveller_flies(1000);

        // THEN
        travellerSteps.traveller_should_have_a_balance_of(10100);

    }
}
