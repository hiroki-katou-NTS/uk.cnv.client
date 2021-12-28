/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailserver;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class PopInfo.
 */
@Getter
@Setter
// POP情報
public class PopInfo extends DomainObject implements Comparable<PopInfo> {
	
	/** The server. */
	// サーバ
	private Server server;
	
	/** The use server. */
	// サーバ使用
	private UseServer useServer;
	
	/** The port. */
	// ポート
	private Port port;
	
	/**
	 * Instantiates a new pop info.
	 *
	 * @param server the server
	 * @param useServer the use server
	 * @param port the port
	 */
	public PopInfo(Server server, UseServer useServer, Port port){
		this.server = server;
		this.useServer = useServer;
		this.port = port;
	}
	
	@Override
	public int compareTo(PopInfo o) {
		return Comparator.comparing(PopInfo::getServer)
				.thenComparing(PopInfo::getUseServer)
				.thenComparing(PopInfo::getPort)
				.compare(this, o);
	}
}
