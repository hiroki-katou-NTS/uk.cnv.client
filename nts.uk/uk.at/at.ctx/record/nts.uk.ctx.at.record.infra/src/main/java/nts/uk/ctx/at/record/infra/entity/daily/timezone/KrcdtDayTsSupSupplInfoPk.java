package nts.uk.ctx.at.record.infra.entity.daily.timezone;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KrcdtDayTsSupSupplInfoPk implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "SID")
	public String sid;
	
	@Convert(converter = GeneralDateToDBConverter.class)
	@Column(name = "YMD")
	public GeneralDate date;
	
	@Column(name = "SUP_NO")
	public int supNo;
}
