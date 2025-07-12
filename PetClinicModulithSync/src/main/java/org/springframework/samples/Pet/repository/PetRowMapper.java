package org.springframework.samples.Pet.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.Pet.model.Pet;
import org.springframework.samples.Pet.model.PetType;

public class PetRowMapper implements RowMapper<Pet> {
	
	@Override
    public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getInt("id"));
        pet.setName(rs.getString("name"));
        
        // Mapear birth_date para LocalDate
        java.sql.Date sqlBirthDate = rs.getDate("birth_date");
        if (sqlBirthDate != null) {
            pet.setBirthDate(sqlBirthDate.toLocalDate());
        }

        pet.setOwner_id(rs.getInt("owner_id")); // Mapear o owner_id diretamente

        // Mapear o PetType usando as colunas do JOIN
        PetType petType = new PetType();
        petType.setId(rs.getInt("type_id"));
        petType.setName(rs.getString("type_name")); // Esta coluna virá do JOIN
        pet.setType(petType);

        return pet;
    }
}
