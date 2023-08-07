package in.globalit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import in.globalit.binding.EligibilityBinding;
import in.globalit.service.EligibilityService;

@RestController
public class EligibilityRestController {

	@Autowired
	private EligibilityService service;

	@GetMapping("/determine/{caseNum}")
	public EligibilityBinding determinEligibility(@PathVariable("caseNum") Long caseNum) {

		return service.determinEligibilityByCaseNum(caseNum);
	}
}
