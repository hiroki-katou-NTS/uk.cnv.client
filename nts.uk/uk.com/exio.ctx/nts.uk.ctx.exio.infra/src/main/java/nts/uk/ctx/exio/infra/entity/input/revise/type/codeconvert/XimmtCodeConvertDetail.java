package nts.uk.ctx.exio.infra.entity.input.revise.type.codeconvert;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert.CodeConvertDetail;
import nts.uk.ctx.exio.infra.entity.input.revise.XimmtReviseItemPK;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  受入コード変換詳細
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "XIMMT_CODE_CONVERT_DETAIL")
public class XimmtCodeConvertDetail extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private XimmtCodeConvertDetailPK pk;
	
	/* 変換後のコード */
	@Column(name = "SYSTEM_CODE")
	private String systemCode;
	
	public static final JpaEntityMapper<XimmtCodeConvertDetail> MAPPER = new JpaEntityMapper<>(XimmtCodeConvertDetail.class);
	
	public static XimmtCodeConvertDetail toEntity(XimmtReviseItemPK parentPk, CodeConvertDetail domain) {
		return new XimmtCodeConvertDetail(
				XimmtCodeConvertDetailPK.of(parentPk, domain.getBefore().v()),
				domain.getAfter().v());
	}
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public CodeConvertDetail toDomain() {
		return new CodeConvertDetail(
				pk.getTargetCode(), 
				systemCode);
	}
}
