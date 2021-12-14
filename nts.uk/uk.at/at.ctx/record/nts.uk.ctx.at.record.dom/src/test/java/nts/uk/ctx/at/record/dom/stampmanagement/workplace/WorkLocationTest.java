package nts.uk.ctx.at.record.dom.stampmanagement.workplace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.gul.location.GeoCoordinate;

@RunWith(JMockit.class)
public class WorkLocationTest {
	
	@Test
	public void getters() {
		
		WorkLocation workLocation = WorkLocationHelper.getDefault();
		NtsAssert.invokeGetters(workLocation);
	}

	@Test
	public void testCanStamptedByMobile_returnTrue() {
		WorkLocation workLocation = WorkLocationHelper.getDefault();
		new MockUp<WorkLocation>() {
			@Mock
			public boolean canStamptedByMobile(GeoCoordinate geoCoordinate) {
				return true;
			}
		};
		boolean check = workLocation.canStamptedByMobile(new GeoCoordinate(100, 200));
		assertThat(check).isTrue();
	}
	
	@Test
	public void testCanStamptedByMobile_returnFalse() {
		WorkLocation workLocation = WorkLocationHelper.getDefault();
		new MockUp<WorkLocation>() {
			@Mock
			public boolean canStamptedByMobile(GeoCoordinate geoCoordinate) {
				return false;
			}
		};
		boolean check = workLocation.canStamptedByMobile(new GeoCoordinate(100, 200));
		assertThat(check).isFalse();
	}


}
