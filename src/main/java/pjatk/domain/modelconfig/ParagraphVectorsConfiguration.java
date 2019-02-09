package pjatk.domain.modelconfig;

import java.util.ArrayList;
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

    public static ParagraphVectorsConfiguration defaultConfiguration(LabelAwareSentenceIterator sentenceIterator,
            List<String> labels) {
        return ParagraphVectorsConfiguration.builder()
                .iterations(20)
                .epochs(5)
                .layerSize(200)
                .seed(42)
                .minWordFrequency(3)
                .windowSize(5)
                .labelAwareSentenceIterator(sentenceIterator)
                .stopWords(new ArrayList<>())
                .tokenizerFactory(Word2VecConfiguration.getDefaultTokenizerFactory())
                .labelsSource(new LabelsSource(labels))
                .build();
    }

    public static TokenizerFactory getDefaultTokenizerFactory() {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return tokenizerFactory;
    }

}
