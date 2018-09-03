package pjatk.doc2vec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataTrainingModel;
import pjatk.domain.data.Tag;
import pjatk.domain.modelconfig.ParagraphVectorsConfiguration;
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
    public ParagraphVectors train(DataBlockFetcher dataBlockFetcher, LabelAwareSentenceIteratorFactory factory) {
        List<DataBlock> dataBlockList = dataBlockFetcher.fetch(dataBlockRepository);
        DataTrainingModel dataTrainingModel = new DataTrainingModel(dataBlockList);
        LabelAwareSentenceIterator sentenceIterator = factory.build(dataTrainingModel);
        List<String> labels = getLabels(dataBlockList);

        ParagraphVectorsConfiguration paragraphVectorsConfiguration = ParagraphVectorsConfiguration
                .defaultConfiguration(sentenceIterator, labels);

        ParagraphVectors paragraphVectors = paragraphVectorsService.train(paragraphVectorsConfiguration);
        dataBlockList.forEach(dataBlock -> {
            dataBlock.setParagraphVectorsUnprocessed(false);
            dataBlock.getTags().forEach(e -> {
                e.setParagraphVectorsProcessed(true);
                tagRepository.save(e);
            });
            dataBlockRepository.save(dataBlock);
        });
        WordVectorSerializer.writeWord2VecModel(paragraphVectors, PARAGRAPH_VECTORS_MODEL_PATH);

        return paragraphVectors;
    }

    private List<String> getLabels(List<DataBlock> dataBlockList) {
        return dataBlockList.stream()
                .map(e -> {
                    Hibernate.initialize(e.getTags());
                    return e.getTags().stream().map(Tag::getContent).collect(Collectors.toList());
                })
                .reduce(new ArrayList<>(), (e,f) -> { e.addAll(f); return e; })
                .stream().distinct().collect(Collectors.toList());
    }
}
