package org.springframework.samples;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchTests {

	private final JavaClasses ownerClasses = new ClassFileImporter()
		.importPackages("org.springframework.samples.Owner");

	private final JavaClasses petClasses = new ClassFileImporter().importPackages("org.springframework.samples.Pet");

	private final JavaClasses vetClasses = new ClassFileImporter().importPackages("org.springframework.samples.Vet");

	private final JavaClasses visitClasses = new ClassFileImporter()
		.importPackages("org.springframework.samples.Visit");

	@Test
	public void serviceAndRepositoryShouldNotAccessApiOwner() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.or()
			.resideInAPackage("service")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(ownerClasses);
	}

	@Test
	public void repositoryShouldNotAccessServiceOwner() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..service..");

		rule.check(ownerClasses);
	}

	@Test
	public void apiShouldNotContainBusinessLogicOwner() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..controller..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..service..");

		rule.check(ownerClasses);
	}

	@Test
	public void packagesShouldNotHaveCyclesOwner() {
		ArchRule rule = slices().matching("org.springframework.samples.(*)..").should().beFreeOfCycles();

		rule.check(ownerClasses);
	}

	@Test
	public void serviceShouldNotAccessApiOwner() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..service..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(petClasses);
	}

	@Test
	public void repositoryShouldNotAccessApiOwner() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(petClasses);
	}

	@Test
	public void repositoryShouldNotAccessServicePet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..service..");

		rule.check(petClasses);
	}

	@Test
	public void apiShouldNotContainBusinessLogicPet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..controller..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..service..");

		rule.check(petClasses);
	}

	@Test
	public void packagesShouldNotHaveCyclesPet() {
		ArchRule rule = slices().matching("org.springframework.samples.(*)..").should().beFreeOfCycles();

		rule.check(petClasses);
	}

	@Test
	public void serviceShouldNotAccessApiVet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..service..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(vetClasses);
	}

	@Test
	public void repositoryShouldNotAccessApiVet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(vetClasses);
	}

	@Test
	public void repositoryShouldNotAccessServiceVet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..service..");

		rule.check(vetClasses);
	}

	@Test
	public void apiShouldNotContainBusinessLogicVet() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..controller..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..service..");

		rule.check(vetClasses);
	}

	@Test
	public void packagesShouldNotHaveCyclesVet() {
		ArchRule rule = slices().matching("org.springframework.samples.(*)..").should().beFreeOfCycles();

		rule.check(vetClasses);
	}

	@Test
	public void serviceShouldNotAccessApiVisit() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..service..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(visitClasses);
	}

	@Test
	public void repositoryShouldNotAccessApiVisit() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..controller..");

		rule.check(visitClasses);
	}

	@Test
	public void repositoryShouldNotAccessServiceVisit() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..repository..")
			.should()
			.accessClassesThat()
			.resideInAPackage("..service..");

		rule.check(visitClasses);
	}

	@Test
	public void apiShouldNotContainBusinessLogicVisit() {
		ArchRule rule = noClasses().that()
			.resideInAPackage("..controller..")
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..service..");

		rule.check(visitClasses);
	}

	@Test
	public void packagesShouldNotHaveCyclesVisit() {
		ArchRule rule = slices().matching("org.springframework.samples.(*)..").should().beFreeOfCycles();

		rule.check(visitClasses);
	}

}
