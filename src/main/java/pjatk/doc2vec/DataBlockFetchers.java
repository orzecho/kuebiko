package pjatk.doc2vec;

import pjatk.persist.DataBlockRepository;

public class DataBlockFetchers {
    public static DataBlockFetcher allUnprocessedBlocks() {
        return DataBlockRepository::findByParagraphVectorsUnprocessedIsTrue;
    }
}
