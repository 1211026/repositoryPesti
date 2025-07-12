package org.springframework.samples.Owner.repository;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.samples.Owner.model.Owner;

import org.springframework.samples.Owner.model.OwnerPet;

import org.springframework.samples.Owner.service.OwnerPetRepository;
import org.springframework.samples.Visit.model.Visit;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.support.GeneratedKeyHolder;

import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;

import java.sql.Statement;

import java.time.LocalDate;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.List;

import java.util.Optional;

import java.util.Set;

import java.util.ArrayList;

import java.util.HashSet;



@Repository

public class OwnerPetRepositoryImpl implements OwnerPetRepository {



private final JdbcTemplate jdbcTemplate;



@Autowired

public OwnerPetRepositoryImpl(@Qualifier("ownerJdbcTemplate") JdbcTemplate jdbcTemplate) {

this.jdbcTemplate = jdbcTemplate;

}



@Override

public List<OwnerPet> findPetByOwnerId(Integer id) {

try {

if (id == null) {

throw new IllegalArgumentException("Owner ID não pode ser nulo");

}


System.out.println("Buscando pets para o owner ID: " + id);


// Consulta SQL para buscar pets por owner_id

String sql = "SELECT id, name, birth_date, type_name, owner_id FROM owner_pets WHERE owner_id = ?";


// Executar a consulta usando JdbcTemplate

List<OwnerPet> pets = jdbcTemplate.query(

sql,

new Object[]{id},

new OwnerPetRowMapper()

);


// Registrar os resultados

if (pets != null && !pets.isEmpty()) {

System.out.println("Encontrados " + pets.size() + " pets para o owner ID: " + id);

for (OwnerPet pet : pets) {

System.out.println("Pet encontrado: " + pet.getName() + " (ID: " + pet.getId() + ")");

}

} else {

System.out.println("Nenhum pet encontrado para o owner ID: " + id + " na consulta SQL");


// Verificar diretamente se o pet existe com esse owner_id

String checkSql = "SELECT COUNT(*) FROM owner_pets WHERE owner_id = ?";

Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);


System.out.println("Contagem direta de pets para o owner ID " + id + ": " + count);

}


return pets;

} catch (Exception e) {

System.err.println("Erro ao buscar pets por owner ID: " + e.getMessage());

e.printStackTrace();

return List.of(); // Retorna lista vazia em caso de erro

}

}



@Override

public Optional<OwnerPet> findById(Integer id) {

try {

if (id == null) {

return Optional.empty();

}


System.out.println("Buscando pet com ID: " + id);


// Consulta SQL para buscar pet por id

String sql = "SELECT id, name, birth_date, type_name, owner_id FROM owner_pets WHERE id = ?";


// Executar a consulta usando JdbcTemplate

List<OwnerPet> pets = jdbcTemplate.query(

sql,

new Object[]{id},

new OwnerPetRowMapper()

);


// Verificar se encontrou algum resultado

if (pets.isEmpty()) {

System.out.println("Nenhum pet encontrado com ID: " + id);

return Optional.empty();

}


OwnerPet pet = pets.get(0);

System.out.println("Pet encontrado: " + pet.getName() +

" (ID: " + pet.getId() +

", Owner ID: " + pet.getOwner_id() + ")");


return Optional.of(pet);

} catch (Exception e) {

System.err.println("Erro ao buscar pet por ID: " + e.getMessage());

e.printStackTrace();

return Optional.empty();

}

}



@Override

public void save(OwnerPet pet) {

if (pet.getId() == null) {

// Inserir novo pet

KeyHolder keyHolder = new GeneratedKeyHolder();

jdbcTemplate.update(connection -> {

PreparedStatement ps = connection.prepareStatement(

"INSERT INTO owner_pets (name, birth_date, type_name, owner_id) VALUES (?, ?, ?, ?)",

Statement.RETURN_GENERATED_KEYS

);

ps.setString(1, pet.getName());


// Converter birthDate para java.sql.Date se necessário

if (pet.getBirthDate() instanceof LocalDate) {

ps.setDate(2, java.sql.Date.valueOf((LocalDate) pet.getBirthDate()));

} else {

ps.setObject(2, pet.getBirthDate());

}


ps.setString(3, pet.getType_name());

ps.setInt(4, pet.getOwner_id());

return ps;

}, keyHolder);


// Obter o ID gerado e atribuir ao pet

Number key = keyHolder.getKey();

if (key != null) {

pet.setId(key.intValue());

System.out.println("Pet salvo com ID gerado: " + pet.getId());

} else {

System.err.println("Falha ao obter ID gerado para o pet");

}

} else {

// Atualizar pet existente

jdbcTemplate.update(

"UPDATE owner_pets SET name = ?, birth_date = ?, type_name = ?, owner_id = ? WHERE id = ?",

pet.getName(),

pet.getBirthDate() instanceof LocalDate ?

java.sql.Date.valueOf((LocalDate) pet.getBirthDate()) : pet.getBirthDate(),

pet.getType_name(),

pet.getOwner_id(),

pet.getId()

);

System.out.println("Pet atualizado com ID: " + pet.getId());

}


// Não tentamos mais atualizar o owner aqui, pois isso deve ser feito em outro nível

// Isso evita a chamada circular que estava causando o erro

}






// Método auxiliar para encontrar um owner pelo ID

private Owner findOwnerById(Integer ownerId) {

try {

return jdbcTemplate.queryForObject(

"SELECT * FROM owners WHERE id = ?",

new BeanPropertyRowMapper<>(Owner.class),

ownerId

);

} catch (EmptyResultDataAccessException e) {

return null;

}

}



@Override

public void save(boolean isNew, OwnerPet pet) {

try {

if (pet == null) {

throw new IllegalArgumentException("Pet não pode ser nulo");

}


// Verificar se o owner_id está definido

if (pet.getOwner_id() == null || pet.getOwner_id() == 0) {

throw new IllegalArgumentException("owner_id não pode ser nulo ou zero");

}


System.out.println("Salvando pet: " + pet.getName() + ", Owner ID: " + pet.getOwner_id() + ", isNew: " + isNew);


if (isNew) {

// Para H2, use IDENTITY() ou SCOPE_IDENTITY() em vez de LAST_INSERT_ID()

// Ou melhor, use o KeyHolder para obter o ID gerado

KeyHolder keyHolder = new GeneratedKeyHolder();


jdbcTemplate.update(connection -> {

PreparedStatement ps = connection.prepareStatement(

"INSERT INTO owner_pets (name, birth_date, owner_id, type_name) VALUES (?, ?, ?, ?)",

Statement.RETURN_GENERATED_KEYS

);

ps.setString(1, pet.getName());

ps.setObject(2, pet.getBirthDate());

ps.setInt(3, pet.getOwner_id());

ps.setString(4, pet.getType_name());

return ps;

}, keyHolder);


Number key = keyHolder.getKey();

if (key != null) {

pet.setId(key.intValue());

System.out.println("Novo ID do pet: " + pet.getId());

}

} else {

// Atualizar pet existente

String sql = "UPDATE owner_pets SET name = ?, birth_date = ?, owner_id = ?, type_name = ? WHERE id = ?";

int rowsAffected = jdbcTemplate.update(

sql,

pet.getName(),

pet.getBirthDate(),

pet.getOwner_id(),

pet.getType_name(),

pet.getId()

);


System.out.println("Atualização de pet: " + rowsAffected + " linhas afetadas");

}


System.out.println("Pet " + (isNew ? "inserido" : "atualizado") + " com sucesso: " + pet.getName());

} catch (Exception e) {

System.err.println("Erro ao salvar pet: " + e.getMessage());

e.printStackTrace();

throw new RuntimeException("Erro ao salvar pet", e);

}

}



@Override

public void save(OwnerPet.Visit visit) {

try {

String sql = "INSERT INTO owner_visits (pet_id, visit_date, description) VALUES (?, ?, ?)";

jdbcTemplate.update(

sql,

visit.getPet_id(),

visit.getVisit_date(),

visit.getDescription()

);


System.out.println("Visita salva com sucesso para o pet ID: " + visit.getPet_id());

} catch (Exception e) {

System.err.println("Erro ao salvar visita: " + e.getMessage());

e.printStackTrace();

throw new RuntimeException("Erro ao salvar visita", e);

}

}



@Override

public Set<OwnerPet.Visit> findVisitByPetId(Integer id) {

try {

String sql = "SELECT id, pet_id, visit_date, description FROM owner_visits WHERE pet_id = ?";

List<OwnerPet.Visit> visits = jdbcTemplate.query(

sql,

new Object[]{id},

(rs, rowNum) -> new OwnerPet.Visit(

rs.getInt("id"),

rs.getString("description"),

rs.getDate("visit_date").toLocalDate(),

rs.getInt("pet_id")

)

);


return new HashSet<>(visits);

} catch (Exception e) {

System.err.println("Erro ao buscar visitas por pet ID: " + e.getMessage());

e.printStackTrace();

return Set.of();

}

}



@Override

public void deletePet(Integer petId) {

try {

String sql = "DELETE FROM owner_pets WHERE id = ?";

int rowsAffected = jdbcTemplate.update(sql, petId);


System.out.println("Pet deletado. ID: " + petId + ", Linhas afetadas: " + rowsAffected);

} catch (Exception e) {

System.err.println("Erro ao deletar pet: " + e.getMessage());

e.printStackTrace();

throw new RuntimeException("Erro ao deletar pet", e);

}

}



@Override

public int countPetsByOwnerId(Integer ownerId) {

try {

String sql = "SELECT COUNT(*) FROM owner_pets WHERE owner_id = ?";

Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ownerId);


return count != null ? count : 0;

} catch (Exception e) {

System.err.println("Erro ao contar pets por owner ID: " + e.getMessage());

e.printStackTrace();

return 0;

}

}



@Override

public List<OwnerPet> findAll() {

try {

String sql = "SELECT id, name, birth_date, type_name, owner_id FROM owner_pets";


return jdbcTemplate.query(sql, new OwnerPetRowMapper());

} catch (Exception e) {

System.err.println("Erro ao buscar todos os pets: " + e.getMessage());

e.printStackTrace();

return List.of();

}

}


// Classe auxiliar para mapear resultados do banco de dados para objetos OwnerPet

private static class OwnerPetRowMapper implements RowMapper<OwnerPet> {

@Override

public OwnerPet mapRow(ResultSet rs, int rowNum) throws SQLException {

OwnerPet pet = new OwnerPet();

pet.setId(rs.getInt("id"));

pet.setName(rs.getString("name"));

pet.setBirthDate(rs.getDate("birth_date").toLocalDate());

pet.setType_name(rs.getString("type_name"));

pet.setOwner_id(rs.getInt("owner_id"));

return pet;

}

}


@Override
public void save(Visit visit) {
	// TODO Auto-generated method stub
	
}






}