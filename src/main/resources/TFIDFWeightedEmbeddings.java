import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.iterator.LineSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ops.impl.transforms.custom.Svd;

import java.io.File;
import java.util.*;

/*
<dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>

*/
public class TFIDFWeightedEmbeddings {
    public static void main(String[] args) throws Exception {
        // Sample sentences (Replace this with your dataset)
        String[] sentences = {
                "Deep learning improves natural language processing.",
                "Word embeddings capture meaning in text.",
                "Skip-Gram is used for training embeddings.",
                "Neural networks power modern NLP models.",
                "Transformers outperform traditional word embeddings."
        };

        // Tokenizer for processing text
        DefaultTokenizerFactory tokenizer = new DefaultTokenizerFactory();

        // Convert sentences into tokenized lists
        List<List<String>> tokenizedSentences = new ArrayList<>();
        for (String sentence : sentences) {
            Tokenizer tok = tokenizer.create(sentence.toLowerCase());
            tokenizedSentences.add(tok.getTokens());
        }

        // Train Word2Vec model using Skip-Gram
        Word2Vec word2Vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(5)
                .layerSize(100) // Vector size
                .seed(42)
                .windowSize(5)
                .tokenizerFactory(tokenizer)
                .useHierarchicalSoftmax(true)
                .build();

        System.out.println("Training Word2Vec...");
        word2Vec.fit();

        // Compute TF-IDF scores
        Map<String, Double> tfidfScores = computeTFIDF(tokenizedSentences);

        // Compute sentence embeddings
        INDArray sentenceMatrix = Nd4j.zeros(sentences.length, 100);
        for (int i = 0; i < sentences.length; i++) {
            sentenceMatrix.putRow(i, getTFIDFWeightedSentenceEmbedding(tokenizedSentences.get(i), word2Vec, tfidfScores));
        }

        // Apply SVD to reduce dimensions
        System.out.println("Applying SVD...");
        INDArray[] svdResult = Nd4j.exec(new Svd(sentenceMatrix, true, true, 50));
        INDArray reducedEmbeddings = svdResult[0]; // Sentence embeddings after dimensionality reduction

        // Print reduced sentence embeddings
        System.out.println("Reduced TF-IDF Weighted Sentence Embeddings:");
        for (int i = 0; i < sentences.length; i++) {
            System.out.println(sentences[i] + " -> " + reducedEmbeddings.getRow(i));
        }
    }

    // Compute TF-IDF scores for words in corpus
    private static Map<String, Double> computeTFIDF(List<List<String>> tokenizedSentences) {
        Map<String, Integer> wordCount = new HashMap<>();
        Map<String, Integer> docFrequency = new HashMap<>();
        int totalDocuments = tokenizedSentences.size();

        // Count term frequency & document frequency
        for (List<String> sentence : tokenizedSentences) {
            Set<String> seenWords = new HashSet<>();
            for (String word : sentence) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                if (!seenWords.contains(word)) {
                    docFrequency.put(word, docFrequency.getOrDefault(word, 0) + 1);
                    seenWords.add(word);
                }
            }
        }

        // Compute TF-IDF scores
        Map<String, Double> tfidfScores = new HashMap<>();
        for (String word : wordCount.keySet()) {
            double tf = (double) wordCount.get(word) / totalDocuments;
            double idf = Math.log((double) totalDocuments / (1 + docFrequency.get(word)));
            tfidfScores.put(word, tf * idf);
        }

        return tfidfScores;
    }

    // Compute TF-IDF weighted sentence embedding
    private static INDArray getTFIDFWeightedSentenceEmbedding(List<String> words, Word2Vec word2Vec, Map<String, Double> tfidfScores) {
        INDArray sentenceVector = Nd4j.zeros(100);
        double totalWeight = 0.0;

        for (String word : words) {
            if (word2Vec.hasWord(word) && tfidfScores.containsKey(word)) {
                double weight = tfidfScores.get(word);
                sentenceVector.addi(word2Vec.getWordVectorMatrix(word).mul(weight));
                totalWeight += weight;
            }
        }

        return totalWeight > 0 ? sentenceVector.div(totalWeight) : sentenceVector; // Normalize by weight sum
    }
}
