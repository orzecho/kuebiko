package pjatk.doc2vec.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pjatk.doc2vec.fetchers.DataBlockFetcher;
import pjatk.doc2vec.fetchers.LabelFetcher;
import pjatk.doc2vec.service.LabelSeekerService;
import pjatk.doc2vec.service.MeansBuilderService;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.ParagraphVectorsResult;
import pjatk.domain.data.Score;
import pjatk.domain.data.Tag;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.ParagraphVectorsResultRepository;
import pjatk.persist.ScoreRepository;
import pjatk.persist.TagRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParagraphVectorsResultService {
    private final LabelSeekerService labelSeekerService;
    private final TagRepository tagRepository;
    private final DataBlockRepository dataBlockRepository;
    private final ParagraphVectorsResultRepository paragraphVectorsResultRepository;
    private final ScoreRepository scoreRepository;
    private final MeansBuilderService meansBuilderService;

    @Transactional
    public void results(ParagraphVectors paragraphVectors,
            DataBlockFetcher dataBlockFetcher, LabelFetcher labelFetcher, String experimentName) {
        List<DataBlock> dataBlocks = dataBlockFetcher.fetch(dataBlockRepository);
        List<String> allLabels = labelFetcher.fetch(tagRepository);

        dataBlocks.forEach(dataBlock -> {
            ParagraphVectorsResult paragraphVectorsResult = paragraphVectorsResultRepository.save(ParagraphVectorsResult
                    .builder()
                    .experimentName(experimentName)
                    .dataBlock(dataBlock)
                    .build());

            try {
                INDArray documentAsCentroid = meansBuilderService.documentAsVector(dataBlock,
                        (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(), paragraphVectors.getTokenizerFactory());
                List<Score> scores = labelSeekerService.getScores(documentAsCentroid, allLabels,
                        (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

                log.info("Document '" + dataBlock.getTags().stream().map(Tag::getContent).collect(Collectors.toList()) + "' falls"
                        + " into the following categories: ");
                scores.stream().sorted(Comparator.comparing(Score::getValue, Comparator.reverseOrder()))
                        .limit(5)
                        .forEach(score -> log.info("        " + score.getLabel() + ": " + score.getValue()));
                scores.forEach(score -> {
                    score.setResult(paragraphVectorsResult);
                    scoreRepository.save(score);
                });
            } catch (ND4JIllegalStateException | IllegalStateException exception) {
                log.error(exception.getMessage());
            }
        });

    }
}
