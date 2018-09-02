package pjatk.doc2vec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.data.Tag;
import pjatk.domain.modelconfig.ParagraphVectorsConfiguration;
import pjatk.domain.modelconfig.Word2VecConfiguration;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.TagRepository;

@Service
@RequiredArgsConstructor
public class ParagraphVectorsTrainingService {
    public static final String PARAGRAPH_VECTORS_MODEL_PATH = "paragraphVectors_model";
    private final ParagraphVectorsService paragraphVectorsService;
    private final DataBlockRepository dataBlockRepository;
    private final TagRepository tagRepository;

    @Transactional
    public ParagraphVectors trainForTheFirstTime() {
        List<DataBlock> dataBlockList = dataBlockRepository.findByParagraphVectorsUnprocessedIsTrue();
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        LabelAwareSentenceIterator sentenceIterator = new LabelAwareDataTrainingModelIterator(dataTrainingModel);
        List<String> labels = dataBlockList.stream()
                .map(e -> {
                    Hibernate.initialize(e.getTags()); //TODO check if its needed
                    return e.getTags().stream().map(Tag::getContent).collect(Collectors.toList());
                })
                .reduce(new ArrayList<>(), (e,f) -> { e.addAll(f); return e; })
                .stream().distinct().collect(Collectors.toList());

        ParagraphVectorsConfiguration paragraphVectorsConfiguration = defaultConfiguration(sentenceIterator, labels);

        ParagraphVectors paragraphVectors = paragraphVectorsService.train(paragraphVectorsConfiguration);
        dataBlockList.forEach(dataBlock -> {
            dataBlock.setParagraphVectorsUnprocessed(false);
            dataBlock.getTags().stream().forEach(e -> {
                e.setParagraphVectorsProcessed(true);
                tagRepository.save(e);
            });
            dataBlockRepository.save(dataBlock);
        });
        WordVectorSerializer.writeWord2VecModel(paragraphVectors, PARAGRAPH_VECTORS_MODEL_PATH);

        return paragraphVectors;
    }

    @Transactional
    public ParagraphVectors trainOnExistingModel() throws IOException {
        ParagraphVectors paragraphVectors = WordVectorSerializer.readParagraphVectors(PARAGRAPH_VECTORS_MODEL_PATH);
        List<DataBlock> dataBlockList = dataBlockRepository.findByParagraphVectorsUnprocessedIsTrue();
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        LabelAwareSentenceIterator sentenceIterator = new LabelAwareDataTrainingModelIterator(dataTrainingModel);

        List<String> labels = dataBlockList.stream()
                .map(e -> {
//                    Hibernate.initialize(e.getTags()); //TODO check if its needed
                    return e.getTags().stream().map(Tag::getContent).collect(Collectors.toList());
                })
                .reduce(new ArrayList<>(), (e,f) -> { e.addAll(f); return e; })
                .stream().distinct().collect(Collectors.toList());

        ParagraphVectorsConfiguration paragraphVectorsConfiguration = defaultConfiguration(sentenceIterator, labels);

        paragraphVectors = paragraphVectorsService.train(paragraphVectors, paragraphVectorsConfiguration);

        dataBlockList.forEach(dataBlock -> {
            dataBlock.setParagraphVectorsUnprocessed(false);
            dataBlockRepository.save(dataBlock);
        });
        WordVectorSerializer.writeWord2VecModel(paragraphVectors, PARAGRAPH_VECTORS_MODEL_PATH);

        return paragraphVectors;
    }

    private ParagraphVectorsConfiguration defaultConfiguration(LabelAwareSentenceIterator sentenceIterator,
            List<String> labels) {
        return ParagraphVectorsConfiguration.builder()
                    .iterations(50) //TODO this should be tailored
                    .epochs(1) //TODO this should be tailored
                    .layerSize(100)
                    .seed(42)
                    .minWordFrequency(3)
                    .windowSize(5)
                    .labelAwareSentenceIterator(sentenceIterator)
                    .stopWords(new ArrayList<>()) //TODO really?
                    .tokenizerFactory(Word2VecConfiguration.getDefaultTokenizerFactory())
                    .labelsSource(new LabelsSource(labels))
                    .build();
    }
}
