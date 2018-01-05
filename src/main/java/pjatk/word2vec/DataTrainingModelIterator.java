package pjatk.word2vec;

import lombok.RequiredArgsConstructor;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Michał Dąbrowski
 */
@RequiredArgsConstructor
public class DataTrainingModelIterator implements SentenceIterator {
    public static final String SPLIT_PATTERN = "[.,!?]+\\s*";
    private final DataTrainingModel dataTrainingModel;
    private Iterator<String> actualDataBlockIterator;
    private Iterator<DataBlock> dataBlockListIterator;

    @Override
    public String nextSentence() {
        return actualDataBlockIterator.next();
    }

    @Override
    public boolean hasNext() {
        initDataBlockListIterator();
        if(nextDataBlockShouldBeLoaded()) {
            if(dataBlockListIterator.hasNext()) {
                actualDataBlockIterator = nextDataBlockIterator();
            } else {
                return false;
            }
        }
        return true;
    }

    private void initDataBlockListIterator() {
        if(dataBlockListIterator == null) {
            dataBlockListIterator = dataTrainingModel.getDataBlockList().listIterator();
        }
    }

    private Iterator<String> nextDataBlockIterator() {
        return Arrays.asList(dataBlockListIterator
                .next()
                .getContent()
                .split(SPLIT_PATTERN))
                .iterator();
    }

    private boolean nextDataBlockShouldBeLoaded() {
        return actualDataBlockIterator == null || !actualDataBlockIterator.hasNext();
    }

    @Override
    public void reset() {
        dataBlockListIterator = dataTrainingModel.getDataBlockList().listIterator();
    }

    @Override
    public void finish() {

    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return null;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {

    }
}
