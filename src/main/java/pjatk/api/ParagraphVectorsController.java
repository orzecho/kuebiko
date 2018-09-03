package pjatk.api;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import pjatk.doc2vec.BestTagInBlockLabelAwareSentenceIteratorFactory;
import static pjatk.doc2vec.DataBlockFetchers.allUnprocessedBlocks;
import static pjatk.doc2vec.DataBlockFetchers.blocksWithBestTags;
import pjatk.doc2vec.IndiscriminateLabelAwareSentenceIteratorFactory;
import static pjatk.doc2vec.LabelFetchers.allProcessedLabels;
import pjatk.doc2vec.ParagraphVectorsResultService;
import pjatk.doc2vec.ParagraphVectorsTrainingService;

@RestController("/paragraph-vectors")
@RequiredArgsConstructor
@Slf4j
public class ParagraphVectorsController {
    private final ParagraphVectorsTrainingService paragraphVectorsTrainingService;
    private final ParagraphVectorsResultService paragraphVectorsResultService;

    @GetMapping("/train-all-in")
    @Transactional
    public void trainAllIn() {
        log.info("Starting training, all data, all labels, without filtering.");
        val factory = new IndiscriminateLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(allUnprocessedBlocks(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(), allProcessedLabels());
    }

    @GetMapping("/train-best-tags-in-data-block")
    @Transactional
    public void trainBestTagsInDataBlock() {
        log.info("Starting training, only choose most popular tag from data block.");
        val factory = new BestTagInBlockLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(allUnprocessedBlocks(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(), allProcessedLabels());
    }

    @GetMapping("/train-best-tags-in-general/one-tag-per-block")
    @Transactional
    public void trainBestTagsInGeneralOneTagPerBlock() {
        log.info("Starting training, get blocks with best tags, one tag per block.");
        val factory = new BestTagInBlockLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(blocksWithBestTags(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(), allProcessedLabels());
    }

    @GetMapping("/train-best-tags-in-general/all-tags-in-block")
    @Transactional
    public void trainBestTagsInGeneral() {
        log.info("Starting training, get blocks with best tags, all tags in block.");
        val factory = new IndiscriminateLabelAwareSentenceIteratorFactory();
        ParagraphVectors paragraphVectors = paragraphVectorsTrainingService.train(blocksWithBestTags(), factory);
        paragraphVectorsResultService.results(paragraphVectors, allUnprocessedBlocks(), allProcessedLabels());
    }
}
