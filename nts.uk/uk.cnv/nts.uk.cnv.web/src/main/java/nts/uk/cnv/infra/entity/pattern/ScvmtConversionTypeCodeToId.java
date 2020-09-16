package nts.uk.cnv.infra.entity.pattern;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.entity.JpaEntity;
import nts.uk.cnv.dom.conversionsql.Join;
import nts.uk.cnv.dom.pattern.CodeToIdPattern;
import nts.uk.cnv.dom.service.ConversionInfo;
import nts.uk.cnv.infra.entity.conversiontable.ScvmtConversionTablePk;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SCVMT_CONVERSION_TYPE_CODE_TO_ID")
public class ScvmtConversionTypeCodeToId extends JpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public ScvmtConversionTablePk pk;

	@Column(name = "SOURCE_NO")
	private int sourceNo;

	@Column(name = "SOURCE_COLUMN_NAME")
	private String sourceColumnName;

	@Column(name = "CODE_TO_ID_TYPE")
	private String codeToIdType;

	@Override
	protected Object getKey() {
		return pk;
	}

	public CodeToIdPattern toDomain(ConversionInfo info, Join sourcejoin) {
		return new CodeToIdPattern(
				info,
				sourcejoin,
				this.sourceColumnName,
				this.codeToIdType);
	}

}
