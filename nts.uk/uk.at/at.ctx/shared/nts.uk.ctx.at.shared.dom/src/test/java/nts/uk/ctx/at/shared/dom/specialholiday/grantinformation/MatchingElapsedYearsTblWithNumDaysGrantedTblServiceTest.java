package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.specialholiday.algorithm.service.MatchingElapsedYearsTblWithNumDaysGrantedTblService;

/**
 * 
 * @author dungbn
 *
 */
@RunWith(JMockit.class)
public class MatchingElapsedYearsTblWithNumDaysGrantedTblServiceTest {

	@Injectable
	private MatchingElapsedYearsTblWithNumDaysGrantedTblService.Require require;
	
	// grantDateTbl.isSpecified() = true
	@Test
	public void testConsistentGrantDaysTbl() {
		
		// input
		ElapseYear elapseYear     = ElapseYearHelper.createElapseYear1();
		GrantDateTbl grantDateTbl = GrantDateTblHelper.createGrantDateTbl();
		
		List<GrantDateTbl> listGrantDateTbl = new ArrayList<GrantDateTbl>();
		GrantDateTbl grantDateTbl1 = GrantDateTblHelper.createGrantDateTbl1();
		GrantDateTbl grantDateTbl2 = GrantDateTblHelper.createGrantDateTbl2();
		listGrantDateTbl.add(grantDateTbl1);
		listGrantDateTbl.add(grantDateTbl2);
		
		new Expectations() {
			{
				require.findBySphdCd("companyId", elapseYear.getSpecialHolidayCode().v());
				result = listGrantDateTbl;
			}
		};
		
		listGrantDateTbl.forEach(e -> {
			if (e.getCompanyId().equals(grantDateTbl.getCompanyId()) 
					&& e.getSpecialHolidayCode().v() == grantDateTbl.getSpecialHolidayCode().v()
					&& e.getGrantDateCode().v().equals(grantDateTbl.getGrantDateCode().v())) {
				
				e.setGrantDateName(grantDateTbl.getGrantDateName());
				e.setElapseYear(grantDateTbl.getElapseYear());
				e.setSpecified(grantDateTbl.isSpecified());
				e.setGrantedDays(grantDateTbl.getGrantedDays());
			}
		});
		
		List<GrantDateTbl> afterMatchingList = elapseYear.matchNumberOfGrantsInGrantDaysTable(listGrantDateTbl);

		if (grantDateTbl.isSpecified()) {
			
			afterMatchingList.forEach(e -> {
				if (!grantDateTbl.getGrantDateCode().v().equals(e.getGrantDateCode().v())) {
					e.setSpecified(false);
				}
			});
			
			afterMatchingList.stream().
				filter(e -> e.isSpecified() == true)
				.forEach(e -> {
					afterMatchingList.forEach(k -> {
						if (k.getCompanyId().equals(e.getCompanyId()) && k.getSpecialHolidayCode().v() == e.getSpecialHolidayCode().v()) {
							k.setSpecified(false);
						}
					});
				});
		}
		
		assertThat(afterMatchingList.get(0).isSpecified()).isFalse();
		assertThat(afterMatchingList.get(1).isSpecified()).isFalse();
		
		val result = MatchingElapsedYearsTblWithNumDaysGrantedTblService.consistentGrantDaysTbl(require, elapseYear, grantDateTbl);
		assertThat(result.size()).isEqualTo(afterMatchingList.size());
		
	}
	
	// grantDateTbl.isSpecified() = false
	@Test
	public void testConsistentGrantDaysTbl1() {
		
		// input
		ElapseYear elapseYear     = ElapseYearHelper.createElapseYear1();
		GrantDateTbl grantDateTbl = GrantDateTblHelper.createGrantDateTbl();
		
		List<GrantDateTbl> listGrantDateTbl = new ArrayList<GrantDateTbl>();
		GrantDateTbl grantDateTbl1 = GrantDateTblHelper.createGrantDateTbl1();
		GrantDateTbl grantDateTbl2 = GrantDateTblHelper.createGrantDateTbl2();
		listGrantDateTbl.add(grantDateTbl1);
		listGrantDateTbl.add(grantDateTbl2);
		
		new Expectations() {
			{
				require.findBySphdCd("companyId", elapseYear.getSpecialHolidayCode().v());
				result = listGrantDateTbl;
			}
		};
		
		listGrantDateTbl.forEach(e -> {
			if (e.getCompanyId().equals(grantDateTbl.getCompanyId()) 
					&& e.getSpecialHolidayCode().v() == grantDateTbl.getSpecialHolidayCode().v()
					&& e.getGrantDateCode().v().equals(grantDateTbl.getGrantDateCode().v())) {
				
				e.setGrantDateName(grantDateTbl.getGrantDateName());
				e.setElapseYear(grantDateTbl.getElapseYear());
				e.setSpecified(grantDateTbl.isSpecified());
				e.setGrantedDays(grantDateTbl.getGrantedDays());
			}
		});
		
		List<GrantDateTbl> afterMatchingListTest = elapseYear.matchNumberOfGrantsInGrantDaysTable(listGrantDateTbl);
		
		assertThat(afterMatchingListTest.get(0).isSpecified()).isTrue();
		assertThat(afterMatchingListTest.get(1).isSpecified()).isTrue();
		
		val result = MatchingElapsedYearsTblWithNumDaysGrantedTblService.consistentGrantDaysTbl(require, elapseYear, grantDateTbl);
		assertThat(result.size()).isEqualTo(afterMatchingListTest.size());
		
	}
}
