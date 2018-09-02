package pjatk.doc2vec;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.ParagraphVectorsResult;
import pjatk.domain.data.Score;
import pjatk.domain.data.Tag;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.ParagraphVectorsResultRepository;
import pjatk.persist.ScoreRepository;
import pjatk.persist.TagRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParagraphVectorsPostTrainingJob {
    private final TagRepository tagRepository;
    private final DataBlockRepository dataBlockRepository;
    private final ParagraphVectorsResultRepository paragraphVectorsResultRepository;
    private final ScoreRepository scoreRepository;

    @Transactional
    public void postTraining(ParagraphVectors paragraphVectors) {
        List<DataBlock> dataBlocks = dataBlockRepository.findByParagraphVectorsUnprocessedIsTrue();
        List<String> allLabels = tagRepository.findByParagraphVectorsProcessedIsTrue().stream()
                .map(Tag::getContent).collect(Collectors.toList());

        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
                paragraphVectors.getTokenizerFactory());
        LabelSeeker seeker = new LabelSeeker(allLabels,
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        dataBlocks.forEach(dataBlock -> {
            ParagraphVectorsResult paragraphVectorsResult = paragraphVectorsResultRepository.save(ParagraphVectorsResult
                    .builder()
                    .experimentName("Bez ograniczeń")
                    .dataBlock(dataBlock)
                    .build());

            INDArray documentAsCentroid = meansBuilder.documentAsVector(dataBlock);
            List<Score> scores = seeker.getScores(documentAsCentroid);

            log.info("Document '" + dataBlock.getTags().stream().map(Tag::getContent).collect(Collectors.toList()) + "' falls"
                    + " into the following categories: ");
            scores.stream().sorted(Comparator.comparing(Score::getValue, Comparator.reverseOrder()))
                    .limit(5)
                    .forEach(score -> log.info("        " + score.getLabel() + ": " + score.getValue()));
            scores.forEach(score -> {
                score.setResult(paragraphVectorsResult);
                scoreRepository.save(score);
            });
        });

    }
}
