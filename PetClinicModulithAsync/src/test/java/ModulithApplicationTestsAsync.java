import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.samples.PetClinicApplication;

public class ModulithApplicationTestsAsync {

	ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);

	@Test
	void shouldBeCompliant() {
		modules.verify();
	}

	@Test
	void writeDocumentationSnippets() {
		new Documenter(modules).writeModuleCanvases().writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
	}

}
