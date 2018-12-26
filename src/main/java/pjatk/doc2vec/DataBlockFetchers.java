package pjatk.doc2vec;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import pjatk.domain.data.DataBlock;
import pjatk.domain.data.Tag;
import pjatk.persist.DataBlockRepository;

public class DataBlockFetchers {
    private static final long TAGS_NUMBER = 10;

    public static DataBlockFetcher allUnprocessedBlocks() {
        return DataBlockRepository::findByParagraphVectorsUnprocessedIsTrue;
    }

    public static DataBlockFetcher allBlocks() {
        return DataBlockRepository::findAll;
    }

    public static DataBlockFetcher halfOfBlocks() {
        return dataBlockRepository -> {
            Long count = dataBlockRepository.count();
            return dataBlockRepository.findAll().stream().skip(count/2).collect(Collectors.toList());
        };
    }

    public static DataBlockFetcher halfOfBlocksWithBestTags() {
        return repository -> {
            Map<Tag, Long> tagCount = new HashMap<>();
            Long count = repository.count();
            List<DataBlock> allDataBlocks = repository.findAll().stream()
                    .skip(count/2).collect(Collectors.toList());
            allDataBlocks.forEach(dataBlock -> dataBlock.getTags().forEach(tag -> {
                if(tagCount.containsKey(tag)) {
                    tagCount.put(tag, tagCount.get(tag) + 1);
                } else {
                    tagCount.put(tag, 1L);
                }
            }));
            Set<Tag> bestTags = tagCount.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                    .limit(TAGS_NUMBER)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            return allDataBlocks.stream()
                    .filter(e -> e.getTags().stream().anyMatch(bestTags::contains))
                    .collect(Collectors.toList());
        };
    }

    public static DataBlockFetcher blocksWithBestTags(final long tagsNumber) {
        return repository -> {
            Map<Tag, Long> tagCount = new HashMap<>();
            List<DataBlock> allDataBlocks = repository.findAll();
            allDataBlocks.forEach(dataBlock -> dataBlock.getTags().forEach(tag -> {
                if(tagCount.containsKey(tag)) {
                    tagCount.put(tag, tagCount.get(tag) + 1);
                } else {
                    tagCount.put(tag, 1L);
                }
            }));
            Set<Tag> bestTags = tagCount.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                    .limit(tagsNumber)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            return allDataBlocks.stream()
                    .filter(e -> e.getTags().stream().anyMatch(bestTags::contains))
                    .collect(Collectors.toList());
        };
    }
}
