package pjatk.word2vec;

import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.word2vec.Word2VecConfiguration;
import pjatk.persist.DataBlockRepository;

@Service
@RequiredArgsConstructor
public class TrainingService {
    public static final String WORD_2_VEC_MODEL_PATH = "word2Vec_model";
    private final Word2VecService word2VecService;
    private final DataBlockRepository dataBlockRepository;

    public Word2Vec trainForTheFirstTime() {
        List<DataBlock> dataBlockList = dataBlockRepository.findByWord2VecUnprocessedIsTrue();
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        SentenceIterator sentenceIterator = new DataTrainingModelIterator(dataTrainingModel);

        Word2VecConfiguration word2VecConfiguration = Word2VecConfiguration.builder()
                .iterations(100)
                .layerSize(100)
                .seed(42)
                .minWordFrequency(3)
                .windowSize(5)
                .sentenceIterator(sentenceIterator)
                .tokenizerFactory(Word2VecConfiguration.getDefaultTokenizerFactory())
                .build();

        Word2Vec word2Vec = word2VecService.train(word2VecConfiguration);
        dataBlockList.forEach(dataBlock -> dataBlock.setWord2VecUnprocessed(true));
        WordVectorSerializer.writeWord2VecModel(word2Vec, WORD_2_VEC_MODEL_PATH);

        return word2Vec;
    }

    public Word2Vec trainOnExistingModel() {
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(WORD_2_VEC_MODEL_PATH);
        List<DataBlock> dataBlockList = dataBlockRepository.findByWord2VecUnprocessedIsTrue();
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        SentenceIterator sentenceIterator = new DataTrainingModelIterator(dataTrainingModel);

        word2Vec.setSentenceIterator(sentenceIterator);
        word2Vec.fit();

        dataBlockList.forEach(dataBlock -> dataBlock.setWord2VecUnprocessed(true));
        WordVectorSerializer.writeWord2VecModel(word2Vec, WORD_2_VEC_MODEL_PATH);

        return word2Vec;
    }
}
