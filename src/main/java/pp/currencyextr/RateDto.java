package pp.currencyextr;

import java.math.BigDecimal;

/**
 * DTO object representing json fetched from NBP currency service
 * 
 * @author pawlacze
 *
 */
public class RateDto implements Comparable<RateDto> {
	private String no;
	private String effectiveDate;
	private BigDecimal mid;

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public BigDecimal getMid() {
		return mid;
	}

	public void setMid(BigDecimal mid) {
		this.mid = mid;
	}

	@Override
	public int compareTo(RateDto o) {
		return this.effectiveDate.compareTo(o.effectiveDate);
	}
}
