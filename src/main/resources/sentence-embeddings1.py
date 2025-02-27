import gensim
import numpy as np
from gensim.models import Word2Vec
from sklearn.decomposition import TruncatedSVD

# Sample corpus (You can replace this with a larger text dataset)
sentences = [
    "Deep learning improves natural language processing.",
    "Word embeddings capture meaning in text.",
    "Skip-Gram is used for training embeddings.",
    "Neural networks power modern NLP models.",
    "Transformers outperform traditional word embeddings."
]

# Tokenize sentences
tokenized_sentences = [sentence.lower().split() for sentence in sentences]

# Train a Word2Vec model using Skip-Gram
word2vec_model = Word2Vec(sentences=tokenized_sentences, vector_size=100, window=5, min_count=1, sg=1, workers=4, seed=42)

# Function to compute sentence embeddings by averaging word vectors
def get_sentence_embedding(sentence, model):
    words = sentence.lower().split()
    word_vectors = [model.wv[word] for word in words if word in model.wv]
    
    if len(word_vectors) == 0:
        return np.zeros(model.vector_size)  # Return zero vector if no words are in the model
    
    return np.mean(word_vectors, axis=0)  # Compute average of word vectors

# Compute sentence embeddings
sentence_embeddings = np.array([get_sentence_embedding(sentence, word2vec_model) for sentence in sentences])

# Apply SVD to reduce dimensionality (e.g., from 100D → 50D)
svd = TruncatedSVD(n_components=50, random_state=42)
reduced_embeddings = svd.fit_transform(sentence_embeddings)

# Print results
print("\nReduced Sentence Embeddings:")
for i, sentence in enumerate(sentences):
    print(f"{sentence} -> {reduced_embeddings[i]}")
