package org.springframework.samples.notifications;

import java.time.LocalDate;

public class SavePetEvent {

	private Integer id;

	private String name;

	private LocalDate birthDate;

	private String type;

	private Integer type_id;

	private Integer owner_id;

	private boolean isNew;

	public SavePetEvent(Integer id, String name, LocalDate birthDate, Integer type_id, String type, Integer owner_id, boolean isNew) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.type = type;
		this.owner_id = owner_id;
		this.type_id = type_id;
		this.isNew = isNew;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(Integer owner_id) {
		this.owner_id = owner_id;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}
}
