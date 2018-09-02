package pjatk.doc2vec;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static pjatk.doc2vec.ParagraphVectorsTrainingService.PARAGRAPH_VECTORS_MODEL_PATH;
import pjatk.persist.DataBlockRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParagraphVectorsTrainingJob {
    private static final Long SUFFICIENT_NUMBER_OF_DATA_BLOCKS = 100l;
    private final ParagraphVectorsTrainingService paragraphVectorsTrainingService;
    private final DataBlockRepository dataBlockRepository;
    private final ParagraphVectorsPostTrainingJob paragraphVectorsPostTrainingJob;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void train() throws InterruptedException, IOException {
        log.info("Starting training job.");
        waitForSufficientNumberOfDataBlocks();
        ParagraphVectors paragraphVectors;

        File modelFile = new File(PARAGRAPH_VECTORS_MODEL_PATH);
        if(modelFile.exists()) {
            log.info("Training on existing model.");
            paragraphVectors = paragraphVectorsTrainingService.trainOnExistingModel();
        } else {
            log.info("Training for the first time.");
            paragraphVectors = paragraphVectorsTrainingService.trainForTheFirstTime();
        }
        paragraphVectorsPostTrainingJob.postTraining(paragraphVectors);
    }

    private void waitForSufficientNumberOfDataBlocks() throws InterruptedException {
        while (dataBlockRepository.countAllByParagraphVectorsUnprocessedIsTrue() < SUFFICIENT_NUMBER_OF_DATA_BLOCKS) {
            log.info("Waiting for data blocks...");
            Thread.sleep(10_0000);
        }
    }
}
