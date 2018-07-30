package pjatk.word2vec;

import java.io.File;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pjatk.persist.DataBlockRepository;
import static pjatk.word2vec.TrainingService.WORD_2_VEC_MODEL_PATH;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingJob {
    private static final Long SUFFICIENT_NUMBER_OF_DATA_BLOCKS = 100l;
    private final TrainingService trainingService;
    private final DataBlockRepository dataBlockRepository;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void train() throws InterruptedException {
        log.info("Starting training job.");
        waitForSufficientNumberOfDataBlocks();
        File modelFile = new File(WORD_2_VEC_MODEL_PATH);
        if(modelFile.exists()) {
            log.info("Training on existing model.");
            trainingService.trainOnExistingModel();
        } else {
            log.info("Training for the first time.");
            trainingService.trainForTheFirstTime();
        }

    }

    private void waitForSufficientNumberOfDataBlocks() throws InterruptedException {
        while (dataBlockRepository.countAllByWord2VecUnprocessedIsTrue() < SUFFICIENT_NUMBER_OF_DATA_BLOCKS) {
            log.info("Waiting for data blocks...");
            Thread.sleep(10_0000);
        }
    }
}
