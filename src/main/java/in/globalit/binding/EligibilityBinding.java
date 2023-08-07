package in.globalit.binding;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EligibilityBinding {
	
	private Integer edTraceId;
	private String planName;
	private String planStatus;
	private LocalDate eligStartDate;
	private LocalDate eligEndDate;
	private String benefitAmount;
	private String denialReason;


}
