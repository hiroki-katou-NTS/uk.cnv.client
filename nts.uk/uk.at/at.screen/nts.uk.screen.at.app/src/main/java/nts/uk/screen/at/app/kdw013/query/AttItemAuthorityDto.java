package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemAuthority;

/**
 * @author thanhpv
 *
 */
@NoArgsConstructor
@Getter
public class AttItemAuthorityDto {
	/** 利用する */
	public Boolean toUse;
	/** 他人が変更できる */
	public Boolean canBeChangedByOthers;
	/** 本人が変更できる */
	public Boolean youCanChangeIt;
	
	public AttItemAuthorityDto(AttItemAuthority domain) {
		super();
		this.toUse = domain.isToUse();
		this.canBeChangedByOthers = domain.isCanBeChangedByOthers();
		this.youCanChangeIt = domain.isYouCanChangeIt();
	}
}
