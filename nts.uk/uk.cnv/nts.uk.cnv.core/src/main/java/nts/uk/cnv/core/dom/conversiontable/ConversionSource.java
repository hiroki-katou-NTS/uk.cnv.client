package nts.uk.cnv.core.dom.conversiontable;

import java.util.ArrayList;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.cnv.core.dom.constants.Constants;
import nts.uk.cnv.core.dom.conversionsql.Join;
import nts.uk.cnv.core.dom.conversionsql.JoinAtr;
import nts.uk.cnv.core.dom.conversionsql.TableFullName;

@Getter
@AllArgsConstructor
public class ConversionSource {

	String sourceId;
	String category;
	String sourceTableName;
	String condition;
	String memo;

	Optional<String> dateColumnName;
	Optional<String> startDateColumnName;
	Optional<String> endDateColumnName;
	Optional<String> dateType;

	public Join getJoin(ConversionInfo info) {
		return new Join(
						new TableFullName(
							info.getSourceDatabaseName(),
							info.getSourceSchema(),
							this.sourceTableName,
							Constants.BaseTableAlias
						),
					JoinAtr.Main,
					new ArrayList<>()
				);
	}
}
