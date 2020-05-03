package com.genome.model;


import java.util.List;

public class GenomeVariants {

	
	private String chromosome;
	private String startPosition;
	private String endPosition;
	private String refAllele;
	private String tumorSeqAllele;
	
	//Object contains list of hgvsp..Kandy
	List<String> hgvsp;
	
	//Setter getter 

	public List<String> getHgvsp() {
		return hgvsp;
	}

	public void setHgvsp(List<String> hgvsp) {
		this.hgvsp = hgvsp;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}

	public String getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(String endPosition) {
		this.endPosition = endPosition;
	}

	public String getRefAllele() {
		return refAllele;
	}

	public void setRefAllele(String refAllele) {
		this.refAllele = refAllele;
	}

	public String getTumorSeqAllele() {
		return tumorSeqAllele;
	}

	public void setTumorSeqAllele(String tumorSeqAllele) {
		this.tumorSeqAllele = tumorSeqAllele;
	}

	@Override
	public String toString() {
		return "GenomeVariants [chromosome=" + chromosome + ", startPosition=" + startPosition + ", endPosition="
				+ endPosition + ", refAllele=" + refAllele + ", tumorSeqAllele=" + tumorSeqAllele + ", hgvsp=" + hgvsp
				+ "]";
	}

	
	
	
}
