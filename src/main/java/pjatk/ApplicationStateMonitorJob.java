package pjatk;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pjatk.persist.DataBlockRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationStateMonitorJob {
    private final DataBlockRepository dataBlockRepository;

    @Scheduled(cron = "*/10 * * * * *")
    public void logState() {
      log.info("Number of unprocessed data blocks: " + dataBlockRepository.countAllByWord2VecUnprocessedIsTrue());
      log.info("Number of all data blocks: " + dataBlockRepository.count());
    }
}
