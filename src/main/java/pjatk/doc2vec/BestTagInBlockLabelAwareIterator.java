package pjatk.doc2vec;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import lombok.extern.slf4j.Slf4j;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.data.Tag;

@Slf4j
public class BestTagInBlockLabelAwareIterator implements LabelAwareSentenceIterator {
    private final DataTrainingModel dataTrainingModel;
    private final Map<Tag, Long> tagCount;

    private Iterator<DataBlock> dataBlockIterator;

    private SentencePreProcessor sentencePreProcessor;

    private List<Tag> currentTags;

    public BestTagInBlockLabelAwareIterator(DataTrainingModel dataTrainingModel) {
        this.dataTrainingModel = dataTrainingModel;
        this.dataBlockIterator = dataTrainingModel.getDataBlockList().iterator();

        this.tagCount = new HashMap<>();
        dataTrainingModel.getDataBlockList().forEach(dataBlock -> dataBlock.getTags().forEach(tag -> {
                if(tagCount.containsKey(tag)) {
                    tagCount.put(tag, tagCount.get(tag) + 1);
                } else {
                    tagCount.put(tag, 1L);
                }
        }));
        log.info("{} tags counted", tagCount.size());
        log.info("Max tag count: {}", tagCount.values().stream().max(Long::compare).orElse(0l));
    }

    @Override
    public String currentLabel() {
        return tagCount.entrySet().stream()
                .filter(entry -> currentTags.contains(entry.getKey()))
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(e -> {
                    e.getKey().setParagraphVectorsProcessed(true);
                    return e.getKey().getContent();
                })
                .orElse("");
    }

    @Override
    public List<String> currentLabels() {
        return null;
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
