package org.springframework.samples.config;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	@Bean
	OpenAPI customOpenAPI() {

		return new OpenAPI().components(new Components())
			.info(new Info().title("Petclinic Api Documentation")
				.version("1.0")
				.termsOfService("Petclinic terms of service")
				.description("This is the API documentation of the Spring Petclinic.")
				.license(swaggerLicense())
				.contact(swaggerContact()));
	}

	private Contact swaggerContact() {
		Contact petclinicContact = new Contact();
		petclinicContact.setName("Miguel Fernandes");
		petclinicContact.setEmail("1211593@isep.ipp.pt");
		petclinicContact.setUrl("https://github.com/miguel1211593/spring_modulith");
		return petclinicContact;
	}

	private License swaggerLicense() {
		License petClinicLicense = new License();
		petClinicLicense.setName("Apache 2.0");
		petClinicLicense.setUrl("http://www.apache.org/licenses/LICENSE-2.0");
		petClinicLicense.setExtensions(Collections.emptyMap());
		return petClinicLicense;
	}

}
