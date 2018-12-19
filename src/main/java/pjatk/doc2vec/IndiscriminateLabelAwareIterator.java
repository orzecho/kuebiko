package pjatk.doc2vec;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.data.Tag;

public class IndiscriminateLabelAwareIterator implements LabelAwareSentenceIterator {
    private final DataTrainingModel dataTrainingModel;

    private Iterator<DataBlock> dataBlockIterator;

    private SentencePreProcessor sentencePreProcessor;

    private List<Tag> currentTags;

    public IndiscriminateLabelAwareIterator(DataTrainingModel dataTrainingModel) {
        this.dataTrainingModel = dataTrainingModel;
        this.dataBlockIterator = dataTrainingModel.getDataBlockList().iterator();
    }

    @Override
    public String currentLabel() {
        return null; // it's multi label problem
    }

    @Override
    public List<String> currentLabels() {
        return currentTags.stream()
                .peek(e -> e.setParagraphVectorsProcessed(true))
                .map(Tag::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public String nextSentence() {
        DataBlock dataBlock = dataBlockIterator.next();
        currentTags = dataBlock.getTags();
        return dataBlock.getContent();
    }

    @Override
    public boolean hasNext() {
        return dataBlockIterator.hasNext();
    }

    @Override
    public void reset() {
        this.dataBlockIterator = dataTrainingModel.getDataBlockList().iterator();
    }

    @Override
    public void finish() {

    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return this.sentencePreProcessor;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {
        this.sentencePreProcessor = preProcessor;
    }
}
