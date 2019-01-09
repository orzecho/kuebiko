package pjatk.api;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import pjatk.doc2vec.iterators.BestTagInBlockLabelAwareSentenceIteratorFactory;
import static pjatk.doc2vec.fetchers.DataBlockFetchers.allUnprocessedBlocks;
import static pjatk.doc2vec.fetchers.DataBlockFetchers.halfOfBlocksWithBestTags;
import static pjatk.doc2vec.fetchers.DataBlockFetchers.halfOfBlocks;
import pjatk.doc2vec.iterators.IndiscriminateLabelAwareSentenceIteratorFactory;
import static pjatk.doc2vec.fetchers.LabelFetchers.allProcessedLabels;
import pjatk.doc2vec.service.ParagraphVectorsResultService;
import pjatk.doc2vec.service.ParagraphVectorsTrainingService;
import pjatk.domain.service.DataBlockService;

@RestController
@RequestMapping("/paragraph-vectors")
@RequiredArgsConstructor
@Slf4j
public class ParagraphVectorsController {
    private final ParagraphVectorsTrainingService paragraphVectorsTrainingService;
    private final ParagraphVectorsResultService paragraphVectorsResultService;
    private final DataBlockService dataBlockService;

    @GetMapping("/train-all-in")
    @Transactional
    public void trainAllIn() {
        log.info("Starting training, all data, all labels, without filtering.");
        dataBlockService.clearUnprocessedFlags();
        val factory = new IndiscriminateLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(halfOfBlocks(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(),
                allProcessedLabels(), "all-in");
    }

    @GetMapping("/train-best-tags-in-data-block")
    @Transactional
    public void trainBestTagsInDataBlock() {
        log.info("Starting training, only choose most popular tag from data block.");
        dataBlockService.clearUnprocessedFlags();
        val factory = new BestTagInBlockLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(halfOfBlocks(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(),
                allProcessedLabels(), "best-tag-in-block");
    }

    @GetMapping("/train-best-tags-in-general/one-tag-per-block")
    @Transactional
    public void trainBestTagsInGeneralOneTagPerBlock() {
        log.info("Starting training, get blocks with best tags, one tag per block.");
        dataBlockService.clearUnprocessedFlags();
        val factory = new BestTagInBlockLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(halfOfBlocksWithBestTags(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(),
                allProcessedLabels(), "only-blocks-with-best-tag-one-tag-per-block");
    }

    @GetMapping("/train-best-tags-in-general/all-tags-in-block")
    @Transactional
    public void trainBestTagsInGeneral() {
        log.info("Starting training, get blocks with best tags, all tags in block.");
        dataBlockService.clearUnprocessedFlags();
        val factory = new IndiscriminateLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(halfOfBlocksWithBestTags(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(),
                allProcessedLabels(), "only-blocks-with-best-tag");
    }
}
