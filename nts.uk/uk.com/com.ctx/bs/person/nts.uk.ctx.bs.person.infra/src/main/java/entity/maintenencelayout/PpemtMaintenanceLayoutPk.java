/**
 * 
 */
package entity.maintenencelayout;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author laitv
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PpemtMaintenanceLayoutPk implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @Column(name = "LAYOUT_ID")
	public String layoutId;
	
}
