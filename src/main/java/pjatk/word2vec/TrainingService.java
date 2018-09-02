package pjatk.word2vec;

import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.modelconfig.Word2VecConfiguration;
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

        Word2VecConfiguration word2VecConfiguration = defaultConfiguration(sentenceIterator);

        Word2Vec word2Vec = word2VecService.train(word2VecConfiguration);
        dataBlockList.forEach(dataBlock -> {
            dataBlock.setWord2VecUnprocessed(false);
            dataBlockRepository.save(dataBlock);
        });
        WordVectorSerializer.writeWord2VecModel(word2Vec, WORD_2_VEC_MODEL_PATH);

        return word2Vec;
    }

    public Word2Vec trainOnExistingModel() {
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(WORD_2_VEC_MODEL_PATH);
        List<DataBlock> dataBlockList = dataBlockRepository.findByWord2VecUnprocessedIsTrue();
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        SentenceIterator sentenceIterator = new DataTrainingModelIterator(dataTrainingModel);

        Word2VecConfiguration word2VecConfiguration = defaultConfiguration(sentenceIterator);

        word2Vec = word2VecService.train(word2Vec, word2VecConfiguration);

        dataBlockList.forEach(dataBlock -> {
            dataBlock.setWord2VecUnprocessed(false);
            dataBlockRepository.save(dataBlock);
        });
        WordVectorSerializer.writeWord2VecModel(word2Vec, WORD_2_VEC_MODEL_PATH);

        return word2Vec;
    }

    private Word2VecConfiguration defaultConfiguration(SentenceIterator sentenceIterator) {
        return Word2VecConfiguration.builder()
                    .iterations(100)
                    .epochs(10)
                    .layerSize(100)
                    .seed(42)
                    .minWordFrequency(3)
                    .windowSize(5)
                    .sentenceIterator(sentenceIterator)
                    .tokenizerFactory(Word2VecConfiguration.getDefaultTokenizerFactory())
                    .build();
    }
}
