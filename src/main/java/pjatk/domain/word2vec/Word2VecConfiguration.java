package pjatk.domain.word2vec;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

/**
 * Class holds configuration about word2vec model.
 * @author Michał Dąbrowski
 */
@Getter
@Builder
public class Word2VecConfiguration {
    private SentenceIterator sentenceIterator;
    private TokenizerFactory tokenizerFactory;
    private Integer minWordFrequency;
    private Integer iterations;
    private Integer layerSize;
    private Integer seed;
    private Integer windowSize;

    public static TokenizerFactory getDefaultTokenizerFactory() {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return tokenizerFactory;
    }
}
