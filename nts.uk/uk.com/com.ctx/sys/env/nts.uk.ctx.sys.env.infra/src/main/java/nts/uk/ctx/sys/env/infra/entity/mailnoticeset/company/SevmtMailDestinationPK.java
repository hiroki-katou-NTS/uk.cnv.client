package nts.uk.ctx.sys.env.infra.entity.mailnoticeset.company;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SevmtMailDestinationPK implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// column 会社ID
    @NonNull
    @Column(name = "CID")
    public String cId;

    // column メール分類
    @NonNull
    @Column(name = "MAIL_CLS")
    public Integer mailClassification;

    // column 機能ID
    @NonNull
    @Column(name = "FUNCTION_ID")
    public Integer funcId;
}
