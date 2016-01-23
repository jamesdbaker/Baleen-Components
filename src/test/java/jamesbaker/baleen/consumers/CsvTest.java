package jamesbaker.baleen.consumers;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;

public class CsvTest {
	@Test
	public void testCsv() throws Exception{
		Path temp = Files.createTempFile("baleen", ".csv");
		
		JCas jCas = JCasFactory.createJCas();
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(Csv.class, Csv.PARAM_OUTPUT_FILE, temp.toString());
		
		DocumentAnnotation docAnnot = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		docAnnot.setSourceUri("test.txt");
		
		jCas.setDocumentText("James went to London");
		
		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.addToIndexes();
		
		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue("London");
		l.addToIndexes();
		
		ae.process(jCas);
		
		String contents = FileUtils.readFileToString(temp.toFile());
		
		assertEquals("\"test.txt\",\"James\",\"Person\",\"0\",\"5\"\n\"test.txt\",\"London\",\"Location\",\"14\",\"20\"\n", contents);
		
		ae.destroy();
		temp.toFile().delete();
	}
	
	@Test
	public void testCsvFiltered() throws Exception{
		Path temp = Files.createTempFile("baleen", ".csv");
		
		JCas jCas = JCasFactory.createJCas();
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(Csv.class, Csv.PARAM_OUTPUT_FILE, temp.toString(), Csv.PARAM_TYPE, new String[]{"uk.gov.dstl.baleen.types.common.Person"});
		
		DocumentAnnotation docAnnot = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		docAnnot.setSourceUri("test.txt");
		
		jCas.setDocumentText("James went to London");
		
		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.addToIndexes();
		
		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue("London");
		l.addToIndexes();
		
		ae.process(jCas);
		
		String contents = FileUtils.readFileToString(temp.toFile());
		
		assertEquals("\"test.txt\",\"James\",\"Person\",\"0\",\"5\"\n", contents);
		
		ae.destroy();
		temp.toFile().delete();
	}
}
