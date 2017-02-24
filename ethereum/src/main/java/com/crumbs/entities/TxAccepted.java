package com.crumbs.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by low on 7/2/17 12:19 AM.
 */
@Table(name = "tx_accepted")
@Entity
@Getter
@Setter
public class TxAccepted extends BasicTx implements Serializable{

	@ManyToOne
	@JoinColumn(name = "sender")
	Member sender;
}
