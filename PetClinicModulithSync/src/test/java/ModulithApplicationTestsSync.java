import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.samples.PetClinicApplicationSync;

public class ModulithApplicationTestsSync {

	ApplicationModules modules = ApplicationModules.of(PetClinicApplicationSync.class);

	@Test
	void shouldBeCompliant() {
		modules.verify();
	}
	@Test
	void writeDocumentationSnippets() {
		new Documenter(modules)
				.writeModuleCanvases()
				.writeModulesAsPlantUml()
				.writeIndividualModulesAsPlantUml();
	}
}
