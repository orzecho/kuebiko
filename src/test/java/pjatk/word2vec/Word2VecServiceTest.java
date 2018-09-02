package pjatk.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pjatk.DataTest;
import pjatk.domain.modelconfig.Word2VecConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Dąbrowski
 */
@SpringBootTest
public class Word2VecServiceTest extends DataTest{

    private Word2VecService word2VecService = new Word2VecService();

    @Test
    public void shouldRunCorrectly() {
        SentenceIterator sentenceIterator = createDataTrainingModelIterator();

        Word2VecConfiguration word2VecConfiguration = Word2VecConfiguration.builder()
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .minWordFrequency(1)
                .windowSize(5)
                .sentenceIterator(sentenceIterator)
                .tokenizerFactory(Word2VecConfiguration.getDefaultTokenizerFactory())
                .build();

        Word2Vec word2Vec = word2VecService.train(word2VecConfiguration);

        assertThat(word2Vec.hasWord("simple")).isTrue();
    }
}
