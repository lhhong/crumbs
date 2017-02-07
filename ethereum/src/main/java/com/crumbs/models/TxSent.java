package com.crumbs.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by low on 2/2/17 11:34 PM.
 */
@Table (name = "tx_sent")
@Entity
@Getter
@Setter
public class TxSent extends BasicTx implements Serializable {

	@ManyToOne
	@JoinColumn(name = "accepter")
	Member accepter;
}
