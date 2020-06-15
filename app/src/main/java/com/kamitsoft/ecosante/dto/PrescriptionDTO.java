package com.kamitsoft.ecosante.dto;



public class PrescriptionDTO {
	private String encounterUuid;
	private String patientUuid;
	private String physistUuid;
	private String patientEmail;
	private String[] emails;
	private int prescriptionType;

	public String getEncounterUuid() {
		return encounterUuid;
	}

	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}

	public String getPatientUuid() {
		return patientUuid;
	}

	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}

	public String getPhysistUuid() {
		return physistUuid;
	}

	public void setPhysistUuid(String physistUuid) {
		this.physistUuid = physistUuid;
	}


	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}



	public int getPrescriptionType() {
		return prescriptionType;
	}

	public void setPrescriptionType(int prescriptionType) {
		this.prescriptionType = prescriptionType;
	}

	public String[] getEmails() {
		return emails;
	}

	public void setEmails(String[] emails) {
		this.emails = emails;
	}
}
