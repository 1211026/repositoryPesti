package org.springframework.samples.Owner.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.samples.Owner.service.OwnerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.samples.Owner.model.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class OwnerRepositoryImpl implements OwnerRepository {

	private final JdbcClient jdbcClient;
	private final DataSource dataSource; // Injeção do DataSource

	public OwnerRepositoryImpl(@Qualifier("ownerJdbcClient") JdbcClient jdbcClient, DataSource dataSource) {
		this.jdbcClient = jdbcClient;
		this.dataSource = dataSource;
	}

	@Override
	public Owner findById(Integer id) {
		return jdbcClient.sql("SELECT * FROM owners WHERE id = ?")
				.param(id)
				.query(Owner.class)
				.optional()
				.orElse(null);
	}

	@Override
	public Page<Owner> findByLastName(String lastName, Pageable pageable) {

		String sql = "SELECT * FROM owners WHERE last_name LIKE :lastName ";

		List<Owner> owners = jdbcClient.sql(sql)
				.param("lastName", lastName + "%")
				.query(Owner.class)
				.list();

		int total = jdbcClient.sql("SELECT COUNT(*) FROM owners WHERE last_name LIKE :lastName")
				.param("lastName", lastName + "%")
				.query(Integer.class)
				.single();

		return new PageImpl<>(owners, pageable, total);
	}


	@Override
	public Owner save(Owner owner) {
		if (owner.getId() == null) {
			try (Connection connection = dataSource.getConnection()) {
			String sql = "INSERT INTO owners (first_name, last_name, address, city, telephone) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			statement.setString(1, owner.getFirstName());
			statement.setString(2, owner.getLastName());
			statement.setString(3, owner.getAddress());
			statement.setString(4, owner.getCity());
			statement.setString(5, owner.getTelephone());

			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				Integer id = generatedKeys.getInt(1);
				owner.setId(id);
			} else {
				throw new SQLException("Failed to get generated keys, no ID obtained.");
			}
			} catch (SQLException ex) {
				ex.printStackTrace(); // Aqui você pode tratar o erro de forma apropriada
			}
        } else {
			jdbcClient.sql("UPDATE owners SET first_name = ?, last_name = ?, address = ?, city = ?, telephone = ? WHERE id = ?")
					.params(owner.getFirstName(), owner.getLastName(), owner.getAddress(), owner.getCity(), owner.getTelephone(), owner.getId())
					.update();
        }
        return owner;
    }

	@Override
	public Optional<Owner> findByName(String firstName, String lastName) {
		return jdbcClient.sql("SELECT * FROM owners WHERE first_name = ? AND last_name = ?")
				.params(firstName, lastName)
				.query(Owner.class)
				.optional();
	}
}
