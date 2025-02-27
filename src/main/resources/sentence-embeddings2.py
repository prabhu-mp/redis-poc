import gensim
import numpy as np
from gensim.models import Word2Vec
from sklearn.decomposition import TruncatedSVD
from sklearn.feature_extraction.text import TfidfVectorizer

# Sample corpus (replace with a real dataset)
sentences = [
    "Deep learning improves natural language processing.",
    "Word embeddings capture meaning in text.",
    "Skip-Gram is used for training embeddings.",
    "Neural networks power modern NLP models.",
    "Transformers outperform traditional word embeddings."
]

# Tokenize sentences
tokenized_sentences = [sentence.lower().split() for sentence in sentences]
flattened_sentences = [" ".join(tokens) for tokens in tokenized_sentences]  # Convert to text for TF-IDF

# Train a Word2Vec model using Skip-Gram
word2vec_model = Word2Vec(sentences=tokenized_sentences, vector_size=100, window=5, min_count=1, sg=1, workers=4, seed=42)

# Compute TF-IDF scores
vectorizer = TfidfVectorizer()
tfidf_matrix = vectorizer.fit_transform(flattened_sentences)
tfidf_scores = dict(zip(vectorizer.get_feature_names_out(), np.asarray(tfidf_matrix.mean(axis=0)).flatten()))

# Function to compute TF-IDF weighted sentence embeddings
def get_tfidf_sentence_embedding(sentence, model, tfidf_scores):
    words = sentence.lower().split()
    word_vectors = []
    weights = []

    for word in words:
        if word in model.wv and word in tfidf_scores:  # Check if word has embedding & TF-IDF score
            word_vectors.append(model.wv[word] * tfidf_scores[word])
            weights.append(tfidf_scores[word])

    if len(word_vectors) == 0:
        return np.zeros(model.vector_size)  # Return zero vector if no words have embeddings

    return np.average(word_vectors, axis=0, weights=weights)  # Compute weighted mean

# Compute sentence embeddings with TF-IDF weighting
sentence_embeddings = np.array([get_tfidf_sentence_embedding(sentence, word2vec_model, tfidf_scores) for sentence in sentences])

# Apply SVD to reduce dimensionality (e.g., from 100D → 50D)
svd = TruncatedSVD(n_components=50, random_state=42)
reduced_embeddings = svd.fit_transform(sentence_embeddings)

# Print results
print("\nReduced TF-IDF Weighted Sentence Embeddings:")
for i, sentence in enumerate(sentences):
    print(f"{sentence} -> {reduced_embeddings[i]}")
