package pjatk.doc2vec.iterators;

import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import pjatk.domain.data.DataTrainingModel;

public class IndiscriminateLabelAwareSentenceIteratorFactory implements LabelAwareSentenceIteratorFactory {
    @Override
    public LabelAwareSentenceIterator build(DataTrainingModel dataTrainingModel) {
        return new IndiscriminateLabelAwareIterator(dataTrainingModel);
    }
}
