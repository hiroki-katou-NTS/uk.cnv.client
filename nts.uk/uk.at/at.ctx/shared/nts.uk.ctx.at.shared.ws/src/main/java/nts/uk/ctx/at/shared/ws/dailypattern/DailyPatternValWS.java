package nts.uk.ctx.at.shared.ws.dailypattern;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.dailypatternval.find.DailyPatternValFinder;
import nts.uk.ctx.at.shared.app.dailypatternval.find.dto.DailyPatternValDto;

/**
 * The Class DailyPatternValWS.
 */
@Path("ctx/at/share/vacation/setting/patternval/")
@Produces("application/json")
public class DailyPatternValWS  extends WebService{
	
	/** The daily pattern val finder. */
	@Inject
	private DailyPatternValFinder dailyPatternValFinder;
	
	/**
	 * Find bypattern cd.
	 *
	 * @param patternCd the pattern cd
	 * @return the list
	 */
	@POST
	@Path("find/patternval/{patternCd}")
	public List<DailyPatternValDto> findBypatternCd(@PathParam("patternCd") String patternCd) {
		return this.dailyPatternValFinder.findPatternValByPatternCd(patternCd);
	}
	
}
