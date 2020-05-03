package com.genome.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.genome.model.GenomeVariants;
import com.genome.services.GenomeUploadService;

@EnableAutoConfiguration
@Controller
public class GenomeController {
	
	@Autowired
	private GenomeUploadService guService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "upload";
    }


    @RequestMapping(value = "genome/fileUpload", method = RequestMethod.POST)
    public String singleFileUpload(@RequestParam("file") MultipartFile file,Model model) {//RedirectAttributes redirectAttributes

    	List<GenomeVariants> GVList;
    	List<GenomeVariants> UpdateGVList= new ArrayList<GenomeVariants>();
    	
    	if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "uploadStatus";
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            GVList = guService.readingGenomeVariant(file.getInputStream());
            
            for(GenomeVariants GVObj :GVList) {
            	//GVObj = guService.restCallGenomeAnotation(GVObj);  //TODO Commemented due to cachiing purpose
            	GVObj = guService.restCallGenomeAnotation(GVObj.getChromosome(),GVObj.getStartPosition(),GVObj.getEndPosition(),GVObj.getRefAllele(),GVObj.getTumorSeqAllele());
            	UpdateGVList.add(GVObj);
                 
            }
            
           // System.out.println("Update GVLIst======="+GVList.toString());

            //redirectAttributes.addFlashAttribute("message",
                  //  "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("message",
                      "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("genomeList", UpdateGVList);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "uploadStatus";
    }
    
}
