package org.springframework.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.samples.Owner.service.OwnerManagement;
import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Owner.service.OwnerRepository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ConventionalTests {
	
	@Autowired
	private OwnerManagement ownerManagement;
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired
	private OwnerPetRepository ownerPetRepository;

	
	@Test
	public void testEditOwnerPhoneNumberShouldBeVisible() {
		Owner owner = new Owner();
		owner.setFirstName("TestOwner");
		owner.setLastName("Conventional");
		owner.setAddress("123 Test St");
		owner.setCity("Test City");
		owner.setTelephone("1234567890");
		
		Integer savedOwnerId = ownerManagement.save(owner);
		assertNotNull(savedOwnerId);
		
		Owner savedOwner = ownerRepository.findById(savedOwnerId);
		assertNotNull(savedOwner);
		
		// Atualizar o número de telefone
		savedOwner.setTelephone("0987654321");
		Integer updatedOwnerId = ownerManagement.save(savedOwner);
		assertNotNull(updatedOwnerId);
		
		Owner updatedOwner = ownerRepository.findById(updatedOwnerId);
		assertNotNull(updatedOwner);
		assertEquals("0987654321", updatedOwner.getTelephone());
	}
	

}
