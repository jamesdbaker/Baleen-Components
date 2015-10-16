package jamesbaker.baleen.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.Buzzword;

/**
 * Find hashtags in the text, and extract them as Buzzwords.
 * 
 * Each buzzword will have the tag 'hashtag' applied,
 * and only hashtags with a length greater than 1 will be extracted
 * (e.g. #test will be found but #1 won't be).
 * 
 * The hash is not included in the extracted value.
 * 
 * @author James Baker
 */
public class HashTag extends AbstractRegexAnnotator<Buzzword> {
	private static final Pattern HASHTAG_PATTERN = Pattern.compile("(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]{2,})");
	
	/**
	 * Create new instance
	 */
	public HashTag(){
		super(HASHTAG_PATTERN, 1, 1.0);
	}

	@Override
	protected Buzzword create(JCas jCas, Matcher matcher) {
		Buzzword bw = new Buzzword(jCas);
		
		StringArray tags = new StringArray(jCas, 1);
		tags.set(0, "hashtag");
		
		bw.setTags(tags);
		
		return bw;
	}
}
