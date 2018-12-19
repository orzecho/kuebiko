package pjatk.domain.service;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.DataBlock;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.TagRepository;

@Service
@RequiredArgsConstructor
public class DataBlockService {
    private final DataBlockRepository dataBlockRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void save(DataBlock dataBlock) {
        if(!dataBlockRepository.findByContentHash(dataBlock.getContentHash()).isPresent()) {
            Hibernate.initialize(dataBlock.getTags());
            dataBlockRepository.save(dataBlock);
        }
    }

    @Transactional
    public void clearUnprocessedFlags() {
        dataBlockRepository.findAll().forEach(dataBlock -> {
            dataBlock.setParagraphVectorsUnprocessed(true);
            dataBlock.setWord2VecUnprocessed(true);
        });
        tagRepository.findAll().forEach(tag -> tag.setParagraphVectorsProcessed(false));
    }
}
