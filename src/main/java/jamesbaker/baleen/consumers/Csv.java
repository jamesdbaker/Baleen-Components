package jamesbaker.baleen.consumers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.opencsv.CSVWriter;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Output entities into a CSV file, with the following columns:
 * <ul>
 * <li>Document Source URI</li>
 * <li>Entity Value</li>
 * <li>Entity Type</li>
 * <li>Entity Start Offset</li>
 * <li>Entity End Offset</li>
 * </ul>
 * 
 * @author James Baker
 * @baleen.javadoc
 */
public class Csv extends BaleenConsumer {

	/**
	 * The file to write results to.
	 * If the file already exists, then it will be overwritten.
	 * 
	 * @baleen.config baleen.csv
	 */
	public static final String PARAM_OUTPUT_FILE = "outputFile";
	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, defaultValue="baleen.csv")
	private String outputFile = "baleen.csv";
	
	/**
	 * A list of entity types, excluding subtypes, to output.
	 * 
	 * If blank, then all entity types will be checked
	 * 
	 * @baleen.config 
	 */
	public static final String PARAM_TYPE = "types";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue={})
	String[] types;
	List<Class<? extends Entity>> classTypes = new ArrayList<>();
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException{		
		JCas jCas;
		try {
			jCas = JCasFactory.createJCas();
		} catch (UIMAException e) {
			throw new ResourceInitializationException(e);
		}
		for(String type : types){
			try{
				classTypes.add(TypeUtils.getEntityClass(type, jCas));
			}catch(BaleenException e){
				getMonitor().error("Couldn't parse type - type will not be included", e);
			}
		}
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation docAnnot = getDocumentAnnotation(jCas);
		
		try(CSVWriter writer = new CSVWriter(new FileWriter(outputFile))){
			
			Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);
			for(Entity e : entities){
				if(classTypes.isEmpty() || classTypes.contains(e.getClass())){
					String[] cols = new String[5];
					
					cols[0] = docAnnot.getSourceUri();
					if(Strings.isNullOrEmpty(e.getValue())){
						cols[1] = e.getCoveredText();
					}else{
						cols[1] = e.getValue();
					}
					cols[2] = e.getType().getShortName();
					cols[3] = String.valueOf(e.getBegin());
					cols[4] = String.valueOf(e.getEnd());
				
					writer.writeNext(cols);
				}
			}
		}catch(IOException ioe){
			getMonitor().error("Unable to write output to file", ioe);
		}
	}

}
