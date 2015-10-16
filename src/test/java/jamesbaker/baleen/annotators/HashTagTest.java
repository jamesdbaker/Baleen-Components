package jamesbaker.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Buzzword;

/**
 * @author James Baker
 */
public class HashTagTest {
	
	@Test
	public void testHashTag() throws Exception{
		JCas jCas = JCasFactory.createJCas();
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(HashTag.class);
		
		jCas.setDocumentText("This is my #example tweet, with a few #validHashTags and a few in#valid ones. We're #1!");
		ae.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, Buzzword.class).size());
		
		Buzzword bw1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
		assertEquals("example", bw1.getCoveredText());
		assertEquals("example", bw1.getValue());
		assertEquals(1, bw1.getTags().size());
		assertEquals("hashtag", bw1.getTags().get(0));
		
		Buzzword bw2 = JCasUtil.selectByIndex(jCas, Buzzword.class, 1);
		assertEquals("validHashTags", bw2.getCoveredText());
		assertEquals("validHashTags", bw2.getValue());
		assertEquals(1, bw2.getTags().size());
		assertEquals("hashtag", bw2.getTags().get(0));
		
		ae.destroy();
	}
}
