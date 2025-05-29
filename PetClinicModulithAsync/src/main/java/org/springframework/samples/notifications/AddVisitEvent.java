package org.springframework.samples.notifications;

import java.time.LocalDate;

public class AddVisitEvent {

	private Integer id;

	private LocalDate date;

	private String description;

	private Integer pet_id;

	public AddVisitEvent(Integer id, LocalDate date, String description, Integer pet_id) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.pet_id = pet_id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPet_id() {
		return pet_id;
	}

	public void setPet_id(Integer pet_id) {
		this.pet_id = pet_id;
	}

}
