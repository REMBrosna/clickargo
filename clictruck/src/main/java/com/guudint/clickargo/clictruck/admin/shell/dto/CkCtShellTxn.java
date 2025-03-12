package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellTxn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellTxn extends AbstractDTO<CkCtShellTxn, TCkCtShellTxn> {

	private static final long serialVersionUID = -7188770957596627837L;
	private String stId;
	private CkCtShellCard TCkCtShellCard;
	private TCoreAccn TCoreAccn;
	private BigDecimal stQty;
	private String stProduct;
	private String stTxnId;
	private Date stTxnDate;
	private BigDecimal stInvNet;
	private BigDecimal stInvGross;
	private BigDecimal stCrvNet;
	private BigDecimal stCrvGross;
	private Character sbStatus;
	private Date sbDtCreate;
	private String sbUidCreate;
	private Date sbDtLupd;
	private String sbUidLupd;

	public CkCtShellTxn() {
	}

	public CkCtShellTxn(TCkCtShellTxn entity) {
		super(entity);
	}

	public CkCtShellTxn(String stId, CkCtShellCard TCkCtShellCard, TCoreAccn TCoreAccn, BigDecimal stQty,
			String stProduct, String stTxnId, Date stTxnDate, BigDecimal stInvNet, BigDecimal stInvGross,
			BigDecimal stCrvNet, BigDecimal stCrvGross, Character sbStatus, Date sbDtCreate, String sbUidCreate,
			Date sbDtLupd, String sbUidLupd) {
		this.stId = stId;
		this.TCkCtShellCard = TCkCtShellCard;
		this.TCoreAccn = TCoreAccn;
		this.stQty = stQty;
		this.stProduct = stProduct;
		this.stTxnId = stTxnId;
		this.stTxnDate = stTxnDate;
		this.stInvNet = stInvNet;
		this.stInvGross = stInvGross;
		this.stCrvNet = stCrvNet;
		this.stCrvGross = stCrvGross;
		this.sbStatus = sbStatus;
		this.sbDtCreate = sbDtCreate;
		this.sbUidCreate = sbUidCreate;
		this.sbDtLupd = sbDtLupd;
		this.sbUidLupd = sbUidLupd;
	}

	/**
	 * @return the stId
	 */
	public String getStId() {
		return stId;
	}

	/**
	 * @param stId the stId to set
	 */
	public void setStId(String stId) {
		this.stId = stId;
	}

	/**
	 * @return the tCkCtShellCard
	 */
	public CkCtShellCard getTCkCtShellCard() {
		return TCkCtShellCard;
	}

	/**
	 * @param tCkCtShellCard the tCkCtShellCard to set
	 */
	public void setTCkCtShellCard(CkCtShellCard tCkCtShellCard) {
		TCkCtShellCard = tCkCtShellCard;
	}

	/**
	 * @return the tCoreAccn
	 */
	public TCoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	/**
	 * @param tCoreAccn the tCoreAccn to set
	 */
	public void setTCoreAccn(TCoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	/**
	 * @return the stQty
	 */
	public BigDecimal getStQty() {
		return stQty;
	}

	/**
	 * @param stQty the stQty to set
	 */
	public void setStQty(BigDecimal stQty) {
		this.stQty = stQty;
	}

	/**
	 * @return the stProduct
	 */
	public String getStProduct() {
		return stProduct;
	}

	/**
	 * @param stProduct the stProduct to set
	 */
	public void setStProduct(String stProduct) {
		this.stProduct = stProduct;
	}

	/**
	 * @return the stTxnId
	 */
	public String getStTxnId() {
		return stTxnId;
	}

	/**
	 * @param stTxnId the stTxnId to set
	 */
	public void setStTxnId(String stTxnId) {
		this.stTxnId = stTxnId;
	}

	/**
	 * @return the stTxnDate
	 */
	public Date getStTxnDate() {
		return stTxnDate;
	}

	/**
	 * @param stTxnDate the stTxnDate to set
	 */
	public void setStTxnDate(Date stTxnDate) {
		this.stTxnDate = stTxnDate;
	}

	/**
	 * @return the stInvNet
	 */
	public BigDecimal getStInvNet() {
		return stInvNet;
	}

	/**
	 * @param stInvNet the stInvNet to set
	 */
	public void setStInvNet(BigDecimal stInvNet) {
		this.stInvNet = stInvNet;
	}

	/**
	 * @return the stInvGross
	 */
	public BigDecimal getStInvGross() {
		return stInvGross;
	}

	/**
	 * @param stInvGross the stInvGross to set
	 */
	public void setStInvGross(BigDecimal stInvGross) {
		this.stInvGross = stInvGross;
	}

	/**
	 * @return the stCrvNet
	 */
	public BigDecimal getStCrvNet() {
		return stCrvNet;
	}

	/**
	 * @param stCrvNet the stCrvNet to set
	 */
	public void setStCrvNet(BigDecimal stCrvNet) {
		this.stCrvNet = stCrvNet;
	}

	/**
	 * @return the stCrvGross
	 */
	public BigDecimal getStCrvGross() {
		return stCrvGross;
	}

	/**
	 * @param stCrvGross the stCrvGross to set
	 */
	public void setStCrvGross(BigDecimal stCrvGross) {
		this.stCrvGross = stCrvGross;
	}

	/**
	 * @return the sbStatus
	 */
	public Character getSbStatus() {
		return sbStatus;
	}

	/**
	 * @param sbStatus the sbStatus to set
	 */
	public void setSbStatus(Character sbStatus) {
		this.sbStatus = sbStatus;
	}

	/**
	 * @return the sbDtCreate
	 */
	public Date getSbDtCreate() {
		return sbDtCreate;
	}

	/**
	 * @param sbDtCreate the sbDtCreate to set
	 */
	public void setSbDtCreate(Date sbDtCreate) {
		this.sbDtCreate = sbDtCreate;
	}

	/**
	 * @return the sbUidCreate
	 */
	public String getSbUidCreate() {
		return sbUidCreate;
	}

	/**
	 * @param sbUidCreate the sbUidCreate to set
	 */
	public void setSbUidCreate(String sbUidCreate) {
		this.sbUidCreate = sbUidCreate;
	}

	/**
	 * @return the sbDtLupd
	 */
	public Date getSbDtLupd() {
		return sbDtLupd;
	}

	/**
	 * @param sbDtLupd the sbDtLupd to set
	 */
	public void setSbDtLupd(Date sbDtLupd) {
		this.sbDtLupd = sbDtLupd;
	}

	/**
	 * @return the sbUidLupd
	 */
	public String getSbUidLupd() {
		return sbUidLupd;
	}

	/**
	 * @param sbUidLupd the sbUidLupd to set
	 */
	public void setSbUidLupd(String sbUidLupd) {
		this.sbUidLupd = sbUidLupd;
	}

	@Override
	public int compareTo(CkCtShellTxn o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
