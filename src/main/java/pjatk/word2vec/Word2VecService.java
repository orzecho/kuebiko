package pjatk.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.stereotype.Service;
import pjatk.domain.modelconfig.Word2VecConfiguration;

/**
 * @author Michał Dąbrowski
 */
@Service
public class Word2VecService {
    public Word2Vec train(Word2VecConfiguration word2VecConfiguration) {
        Word2Vec word2Vec = buildWord2VecModel(word2VecConfiguration);
        word2Vec.fit();
        return word2Vec;
    }

    public Word2Vec train(Word2Vec word2Vec, Word2VecConfiguration word2VecConfiguration) {
        word2Vec.setSentenceIterator(word2VecConfiguration.getSentenceIterator());
        word2Vec.setTokenizerFactory(word2VecConfiguration.getTokenizerFactory());
        word2Vec.fit();
        return word2Vec;
    }

    private Word2Vec buildWord2VecModel(Word2VecConfiguration word2VecConfiguration) {
        return new Word2Vec.Builder()
                .minWordFrequency(word2VecConfiguration.getMinWordFrequency())
                .iterations(word2VecConfiguration.getIterations())
                .epochs(word2VecConfiguration.getEpochs())
                .layerSize(word2VecConfiguration.getLayerSize())
                .seed(word2VecConfiguration.getSeed())
                .windowSize(word2VecConfiguration.getWindowSize())
                .iterate(word2VecConfiguration.getSentenceIterator())
                .tokenizerFactory(word2VecConfiguration.getTokenizerFactory())
                .build();
    }
}
