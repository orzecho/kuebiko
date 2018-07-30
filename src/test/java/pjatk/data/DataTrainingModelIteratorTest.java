package pjatk.data;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import pjatk.DataTest;
import pjatk.word2vec.DataTrainingModelIterator;

/**
 * @author Michał Dąbrowski
 */
@SpringBootTest
public class DataTrainingModelIteratorTest extends DataTest {

    @Test
    public void shouldReturnSentencesFromSingleDataBlock() {
        DataTrainingModelIterator dataTrainingModelIterator = createDataTrainingModelIterator();

        assertThat(dataTrainingModelIterator.hasNext()).isTrue();
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Another simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("One another simple sentence");
        assertThat(dataTrainingModelIterator.hasNext()).isFalse();
    }

    @Test
    public void shouldReturnSentencesFromMultipleDataBlocks() {
        DataTrainingModelIterator dataTrainingModelIterator = createDataTrainingModelIterator(2);

        assertThat(dataTrainingModelIterator.hasNext()).isTrue();
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Another simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("One another simple sentence");
        assertThat(dataTrainingModelIterator.hasNext()).isTrue();
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("Another simple sentence");
        assertThat(dataTrainingModelIterator.nextSentence()).isEqualTo("One another simple sentence");
        assertThat(dataTrainingModelIterator.hasNext()).isFalse();
    }
}
