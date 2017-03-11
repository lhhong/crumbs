package com.crumbs.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by low on 4/2/17 3:38 PM.
 */

@Entity
@Table (name = "contract_metadata")
@Getter
@Setter
public class CrumbsContract {
	@Id
	private String contractName;
	private byte[] txHash;
	private byte[] contractAddr;
	private boolean included = false;

	@Lob
	@Column (length = 8195)
	private String bin;
	@Lob
	@Column (length = 8195)
	private String abi;

	//metadata and solc no longer needed
	//Left untouched for compatibility with older data

	@Lob
	@Column (length = 8195)
	private String metadata;
	@Lob
	@Column (length = 8195)
	private String solc;
}
