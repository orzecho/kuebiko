package pjatk.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static pjatk.doc2vec.fetchers.DataBlockFetchers.blocksWithBestTags;
import pjatk.domain.data.DataBlock;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.TagRepository;

@RestController
@RequestMapping("/prune")
@RequiredArgsConstructor
@Slf4j
public class PruneController {
    private final DataBlockRepository dataBlockRepository;
    private final TagRepository tagRepository;

    @Transactional
    @GetMapping
    public void prune() {
        List<DataBlock> goodBlocks = blocksWithBestTags(30).fetch(dataBlockRepository);
        List<DataBlock> badBlocks = dataBlockRepository.findAll().stream()
                .filter(e -> !goodBlocks.contains(e))
                .collect(Collectors.toList());
        log.info("Blocks to delete: " + badBlocks.size());
        log.info("Blocks to keep: " + goodBlocks.size());
        dataBlockRepository.deleteAll(badBlocks);
    }
}
