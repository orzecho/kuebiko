package pjatk.doc2vec.iterators;

import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import pjatk.domain.data.DataTrainingModel;

public interface LabelAwareSentenceIteratorFactory {
    LabelAwareSentenceIterator build(DataTrainingModel dataTrainingModel);
}
