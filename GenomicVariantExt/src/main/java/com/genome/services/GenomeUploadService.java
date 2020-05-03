package com.genome.services;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genome.model.GenomeVariants;

@Service
public class GenomeUploadService {
	
	
	private RestTemplate restTemplate = new RestTemplate();
	private final String baseURL = "https://www.genomenexus.org/annotation/genomic/";
	private ObjectMapper mapper = new ObjectMapper();

	public List<GenomeVariants> readingGenomeVariant(InputStream inputStream) throws IOException {
		
		GenomeVariants genomeVariants;
		List<GenomeVariants> gvList= new ArrayList<GenomeVariants>();
		String colVal="";
		
		XSSFWorkbook myExcelBook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet=myExcelBook.getSheetAt(0);  
		//evaluating cell type   
		FormulaEvaluator formulaEvaluator=myExcelBook.getCreationHelper().createFormulaEvaluator();  
		for(Row row: sheet)    
		{ 
			if(row.getRowNum()!=0) {
				genomeVariants=new GenomeVariants();
				int count=0;
			for(Cell cell: row)    
			{  
				switch(formulaEvaluator.evaluateInCell(cell).getCellType())  
				{ 
				    case Cell.CELL_TYPE_NUMERIC:     
					//System.out.print(cell.getNumericCellValue()+ "\t\t"); 
					colVal=(int)cell.getNumericCellValue()+"";
					break;  
					case Cell.CELL_TYPE_STRING:   
					//System.out.print(cell.getStringCellValue()+ "\t\t");  
					colVal=cell.getStringCellValue();
					break;  
					case Cell.CELL_TYPE_BLANK :
					//System.out.print( "NA \t\t");  
					colVal=null;
					break;
				}
				if(count==0) {
					genomeVariants.setChromosome(colVal);	
				}else if(count==1) {
					genomeVariants.setStartPosition(colVal);
				}else if(count==2) {
					genomeVariants.setEndPosition(colVal);
				}else if(count==3) {
					genomeVariants.setRefAllele(colVal);
				}else if(count==4) {
					genomeVariants.setTumorSeqAllele(colVal);
				}
				
				count=count+1;
			}
			gvList.add(genomeVariants);
			
		}
		}
		myExcelBook.close();
		return gvList;
	}
	
	@Cacheable("GenomeVariants")
	//public static GenomeVariants restCallGenomeAnotation(GenomeVariants GVObj) {
	public  GenomeVariants restCallGenomeAnotation(String chromosome,String startPos,String endPos,String refAllele,String tumorSeqAllele) {	
		//System.out.println("GVObject="+GVObj.toString());
		
		    GenomeVariants GVObj= new GenomeVariants();
		    GVObj.setChromosome(chromosome);
		    GVObj.setStartPosition(startPos);
		    GVObj.setEndPosition(endPos);
		    GVObj.setRefAllele(refAllele);
		    GVObj.setTumorSeqAllele(tumorSeqAllele);
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		    
		    //String subDomailUri= GVObj.getChromosome()+","+GVObj.getStartPosition()+","+GVObj.getEndPosition()+","+GVObj.getRefAllele()+","+GVObj.getTumorSeqAllele();
		    
		    String subDomailUri= chromosome+","+startPos+","+endPos+","+refAllele+","+tumorSeqAllele;

		    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseURL+subDomailUri)
		            .queryParam("fields", "hotspots,mutation_assessor");
		    
		    HttpEntity<?> entity = new HttpEntity<>(headers);
		  
		     //System.out.println("Builder String =="+builder.toUriString());
		     
		    HttpEntity<String> response= null;
		    try { 
		    	response = restTemplate.exchange(
			            builder.toUriString(), 
			            HttpMethod.GET, 
			            entity, 
			            String.class);
		    	
		    	//System.out.println("response try >>>>>>"+response.getBody());
		    	//Calling Method for Extraction of HGVP...Kandy
		    	GVObj.setHgvsp(extractHgvspResponse(response.getBody()));
		    	
		    }catch (Exception e) {
		    	return GVObj;
			}
		  return GVObj;
	}
	
	
	public List<String> extractHgvspResponse(String response) throws JsonMappingException, JsonProcessingException{
		ArrayList<String> hgvspList= new ArrayList<String>();
    	
    	Map nodes = mapper.readValue(response, HashMap.class);
        
            if (nodes.containsKey("transcript_consequences")) {

            	Map[] tscNode = mapper.readValue(mapper.writeValueAsString(nodes.get("transcript_consequences")), HashMap[].class);
            	
            	for(Map tsc : tscNode) {
        
            		if(tsc.containsKey("hgvsp")) {
            			hgvspList.add(mapper.writeValueAsString(tsc.get("hgvsp")));
            		}
            		
            	}
            }
        
    	return hgvspList;
		
	}


	

}
