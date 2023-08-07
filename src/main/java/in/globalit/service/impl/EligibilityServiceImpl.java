package in.globalit.service.impl;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.globalit.binding.EligibilityBinding;
import in.globalit.entity.CitizenApplicationEntity;
import in.globalit.entity.CitizenEducationEntity;
import in.globalit.entity.CitizenIncomeEntity;
import in.globalit.entity.CitizenKidsEntity;
import in.globalit.entity.EligibilityDeterminationEntity;
import in.globalit.repository.CitizenEducationRepo;
import in.globalit.repository.CitizenIncomeRepo;
import in.globalit.repository.CitizenKidsRepo;
import in.globalit.repository.CitizenRepo;
import in.globalit.repository.EligibilityRepo;
import in.globalit.service.EligibilityService;

@Service
public class EligibilityServiceImpl implements EligibilityService {

	@Autowired
	private CitizenRepo citizenRepo;

	@Autowired
	private CitizenEducationRepo educationRepo;

	@Autowired
	private CitizenIncomeRepo incomeRepo;

	@Autowired
	private CitizenKidsRepo kidsRepo;

	@Autowired
	private EligibilityRepo eligRepo;

	@Override
	public EligibilityBinding determinEligibilityByCaseNum(Long caseNum) {

		Optional<CitizenApplicationEntity> findById = citizenRepo.findById(caseNum);
		if (findById.isPresent()) {
			CitizenApplicationEntity applicationEntity = findById.get();
			Integer citizenAge = Period.between(applicationEntity.getDob(), LocalDate.now()).getYears();
			String planName = applicationEntity.getPlanName();
			CitizenEducationEntity educationEntity = educationRepo.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			// get income details
			CitizenIncomeEntity incomeEntity = incomeRepo.findByCaseNum(caseNum);
			// get and add all income of citizen
			Double monthlySalary = incomeEntity.getMonthlySalary();
			Double propertyIncome = incomeEntity.getPropertyIncome();
			Double rentIncome = incomeEntity.getRentIncome();
			Double totalIncome = monthlySalary + propertyIncome + rentIncome;

			List<CitizenKidsEntity> kidsList = kidsRepo.findByCaseNum(caseNum);
			// getting kids age
			List<Integer> kidsAge = new ArrayList<>();
			kidsList.forEach(kid -> {
				Integer kidAge = kid.getKidAge();
				// if kids age is in dob then Integer kidAge =
				// Period.between(kid.getDob(),LocalDate.now()).getYears();
				kidsAge.add(kidAge);
			});
			boolean kidsAgeGreaterThan16 = kidsAge.stream().anyMatch(age -> age >= 16);
			EligibilityDeterminationEntity eligEntity = new EligibilityDeterminationEntity();

			EligibilityBinding binding = new EligibilityBinding();
			EligibilityDeterminationEntity eligibilityDeterminationEntity = eligRepo.findByCaseNum(caseNum);
			if (eligibilityDeterminationEntity == null) {

				if ("SNAP".equals(planName)) {
					if (totalIncome < 300) {
						double benefitAmount = 350.00;
						String dollarCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(benefitAmount);
						eligEntity.setBenefitAmount(dollarCurrency);

						saveApprovedDetails(applicationEntity, planName, eligEntity);
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);

						BeanUtils.copyProperties(save, binding);
						return binding;
					} else {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry your total income is greater than $300..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;
					}

				}
				if ("CCAP".equals(planName)) {
					if (totalIncome > 500) {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry your total income is greater than $500..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;
					}
					if (kidsAgeGreaterThan16) {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry your kid age is greater than 16..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;

					} else {
						double benefitAmount = 250.00;
						String dollarCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(benefitAmount);
						eligEntity.setBenefitAmount(dollarCurrency);

						saveApprovedDetails(applicationEntity, planName, eligEntity);
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);

						BeanUtils.copyProperties(save, binding);
						return binding;
					}
				}
				if ("Medicaid".equals(planName)) {
					if (totalIncome < 320) {
						double benefitAmount = 250.00;
						String dollarCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(benefitAmount);
						eligEntity.setBenefitAmount(dollarCurrency);

						saveApprovedDetails(applicationEntity, planName, eligEntity);
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);

						BeanUtils.copyProperties(save, binding);
						return binding;

					} else {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry your total income is greater than $320..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;
					}
				}
				if ("Medicare".equals(planName)) {
					if (citizenAge < 65) {
						double benefitAmount = 250.00;
						String dollarCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(benefitAmount);
						eligEntity.setBenefitAmount(dollarCurrency);

						saveApprovedDetails(applicationEntity, planName, eligEntity);
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);

						BeanUtils.copyProperties(save, binding);
						return binding;
					} else {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry your total income is greater than $320..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;
					}
				}
				if ("RIW".equals(planName)) {
					if (monthlySalary > 0) {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry you have a monthly salary..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;
					}
					if (graduationYear == null) {
						saveDeniedDetails(applicationEntity, planName, eligEntity);

						eligEntity.setDenialReason(
								"Sorry gradualtion is mandatory..so,you are not eligible for this plan ");
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);
						BeanUtils.copyProperties(save, binding);
						return binding;

					} else {
						double benefitAmount = 250.00;
						String dollarCurrency = NumberFormat.getCurrencyInstance(Locale.US).format(benefitAmount);
						eligEntity.setBenefitAmount(dollarCurrency);

						saveApprovedDetails(applicationEntity, planName, eligEntity);
						EligibilityDeterminationEntity save = eligRepo.save(eligEntity);

						BeanUtils.copyProperties(save, binding);
						return binding;

					}
				}

			} else {
				BeanUtils.copyProperties(eligibilityDeterminationEntity, binding);
				return binding;
			}
		}
		return null;
	}

	private void saveDeniedDetails(CitizenApplicationEntity applicationEntity, String planName,
			EligibilityDeterminationEntity eligEntity) {
		eligEntity.setApplication(applicationEntity);
		eligEntity.setPlanName(planName);
		eligEntity.setPlanStatus("DENIED");
	}

	private void saveApprovedDetails(CitizenApplicationEntity applicationEntity, String planName,
			EligibilityDeterminationEntity eligEntity) {
		eligEntity.setApplication(applicationEntity);
		eligEntity.setEligStartDate(LocalDate.now().plusWeeks(2));
		eligEntity.setEligEndDate(LocalDate.now().plusWeeks(2).plusMonths(6));
		eligEntity.setPlanName(planName);
		eligEntity.setPlanStatus("APPROVED");
	}

}
