package nts.uk.ctx.sys.env.infra.entity.mailnoticeset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "SEVMT_MAIL_FUNCTION")
@Getter
@Setter
public class SevmtMailFunction extends UkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
   
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    
    @Id
    @Column(name = "FUNCTION_ID")
    private Integer functionId;

    @Column(name = "FUNCTION_NAME")
    private String functionName;
    
    @Column(name = "SEND_MAIL_SET_ATR")
    private int sendMailSetAtr;
    
    @Column(name = "SORT_ORDER")
    private int sortOrder;

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (functionId != null ? functionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SevmtMailFunction)) {
            return false;
        }
        SevmtMailFunction other = (SevmtMailFunction) object;
        if ((this.functionId == null && other.functionId != null) || (this.functionId != null && !this.functionId.equals(other.functionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication1.SevstMailFunction[ functionId=" + functionId + " ]";
    }

	@Override
	protected Object getKey() {
		return this.functionId;
	}

	public SevmtMailFunction() {
		super();
	}
    
}
