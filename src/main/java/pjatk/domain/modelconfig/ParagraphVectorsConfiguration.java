package pjatk.domain.modelconfig;

import java.util.List;

import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import lombok.Builder;
import lombok.Getter;

/**
 * Class holds configuration about paragraphVectors model.
 * @author Michał Dąbrowski
 */
@Getter
@Builder
public class ParagraphVectorsConfiguration {
    private TokenizerFactory tokenizerFactory;
    private LabelAwareSentenceIterator labelAwareSentenceIterator;
    private Integer minWordFrequency;
    private Integer iterations;
    private Integer epochs;
    private List<String> stopWords;
    private LabelsSource labelsSource;
    private Integer layerSize;
    private Integer seed;
    private Integer windowSize;

    public static TokenizerFactory getDefaultTokenizerFactory() {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return tokenizerFactory;
    }
}
