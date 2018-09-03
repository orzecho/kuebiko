package pjatk.doc2vec;

import java.util.List;

import pjatk.domain.data.DataBlock;
import pjatk.persist.DataBlockRepository;

@FunctionalInterface
public interface DataBlockFetcher {
    List<DataBlock> fetch(DataBlockRepository dataBlockRepository);
}
